package kg.home.demo.service.impl;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import kg.home.demo.entity.*;
import kg.home.demo.exception.*;
import kg.home.demo.mapper.*;
import kg.home.demo.model.*;
import kg.home.demo.repository.*;
import kg.home.demo.service.ReportTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportTemplateServiceImpl implements ReportTemplateService {
    private final MdReportInRepository reports;
    private final MdReportInReferenceRepository references;
    private final MdReportInReferenceItemRepository fields;
    private final DrReferenceRepository dictionaries;
    private final DrReferenceItemRepository dictionaryItems;
    private final DrTypePositionRepository positions;
    private final DrTypeFieldRepository fieldTypes;
    private final DrTypePeriodRepository periodTypes;
    private final DrTypeEnterpriseRepository enterpriseTypes;
    private final TbReferenceItemRepository dataReferences;
    private final TbReferenceItemValueRepository dataValues;
    private final MdReportInMapper reportMapper;
    private final MdReportInReferenceMapper referenceMapper;
    private final MdReportInReferenceItemMapper fieldMapper;
    private final DrTypePositionMapper positionMapper;
    private final DrTypeFieldMapper fieldTypeMapper;
    private final Validator validator;

    @Override
    public PageResult<MdReportIn> findPage(Pageable pageable) {
        return PageResult.from(reports.findAll(pageable).map(reportMapper::toModel));
    }

    @Override
    public ReportTemplate get(Long id) {
        return snapshot(required(reports.findById(id), "Входящий отчёт", id));
    }

    @Override
    public ReportDesignerLookups lookups() {
        return new ReportDesignerLookups(
                positions.findAll(org.springframework.data.domain.Sort.by("id")).stream()
                        .map(positionMapper::toModel).toList(),
                fieldTypes.findAll(org.springframework.data.domain.Sort.by("id")).stream()
                        .map(fieldTypeMapper::toModel).toList());
    }

    public static boolean isHorizontal(DrTypePositionEntity position) {
        String name = position.getNameTypePosition().strip().toLowerCase(Locale.ROOT);
        if (name.startsWith("горизонт") || name.equals("horizontal")) return true;
        if (name.startsWith("вертик") || name.equals("vertical")) return false;
        throw invalid("Неизвестное расположение справочника: " + position.getNameTypePosition());
    }

    @Override
    public void validate(ReportTemplateRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);
        if (request.report().indexRowScanEnd() > 1048576) {
            throw invalid("Номер строки Excel не может превышать 1048576.");
        }
        required(periodTypes.findById(request.report().typePeriod().id()), "Тип периода",
                request.report().typePeriod().id());
        required(enterpriseTypes.findById(request.report().typeEnterprise().id()), "Тип предприятия",
                request.report().typeEnterprise().id());
        Map<Long, ReportTemplateRequest.ReferenceInput> refs = new HashMap<>();
        Set<Long> referenceIds = new HashSet<>();
        Set<Long> verticalColumns = new HashSet<>();
        Set<Long> horizontalReferences = new HashSet<>();
        for (var ref : request.references()) {
            if (ref.id() != null && !referenceIds.add(ref.id())) throw invalid("Повторяется ID справочника отчёта.");
            if (refs.put(ref.referenceId(), ref) != null) throw invalid("Справочник добавлен в отчёт дважды.");
            required(dictionaries.findById(ref.referenceId()), "Справочник", ref.referenceId());
            var position = required(positions.findById(ref.typePositionId()), "Расположение", ref.typePositionId());
            if (isHorizontal(position)) {
                horizontalReferences.add(ref.referenceId());
            } else {
                if (ref.indexNum() > 16384) throw invalid("Номер столбца Excel не может превышать 16384.");
                if (!verticalColumns.add(ref.indexNum())) throw invalid("Два вертикальных справочника занимают один столбец.");
            }
        }
        Set<Long> fieldIds = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();
        Set<Long> fieldColumns = new HashSet<>();
        Set<Long> populatedReferences = new HashSet<>();
        for (var field : request.fields()) {
            if (field.id() != null && !fieldIds.add(field.id())) throw invalid("Повторяется ID поля отчёта.");
            if (!itemIds.add(field.referenceItemId())) throw invalid("Значение справочника добавлено в отчёт дважды.");
            var item = required(dictionaryItems.findById(field.referenceItemId()), "Значение справочника",
                    field.referenceItemId());
            Long referenceId = item.getReference().getId();
            if (!horizontalReferences.contains(referenceId)) {
                throw invalid("Поле «" + item.getNameReferenceItem() + "» должно принадлежать горизонтальной шапке отчёта.");
            }
            populatedReferences.add(referenceId);
            var type = required(fieldTypes.findById(field.typeFieldValueId()), "Тип данных", field.typeFieldValueId());
            if (!Set.of("NUMBER", "STRING", "DATETIME").contains(type.getCodeTypeField())) {
                throw invalid("Поддерживаются типы данных NUMBER, STRING и DATETIME.");
            }
            if (!fieldColumns.add(field.indexColumn()) || verticalColumns.contains(field.indexColumn())) {
                throw invalid("Столбец Excel " + field.indexColumn() + " назначен нескольким элементам.");
            }
        }
        if (!populatedReferences.containsAll(horizontalReferences)) {
            throw invalid("Добавьте поля в каждую горизонтальную шапку.");
        }
    }

    @Override
    @Transactional
    public ReportTemplate create(ReportTemplateRequest request) {
        validate(request);
        if (request.references().stream().anyMatch(ref -> ref.id() != null) ||
                request.fields().stream().anyMatch(field -> field.id() != null)) {
            throw invalid("Новый отчёт не может содержать ID элементов другого отчёта.");
        }
        var report = new MdReportInEntity();
        reportMapper.applyRequest(report, request.report());
        reports.saveAndFlush(report);
        writeChildren(report, request, List.of(), List.of());
        return snapshot(report);
    }

    @Override
    @Transactional
    public ReportTemplate update(Long id, ReportTemplateRequest request) {
        var report = required(reports.findForUpdate(id), "Входящий отчёт", id);
        if (!Objects.equals(snapshot(report).revision(), request.revision())) {
            throw new TemplateConflictException("Отчёт изменён после открытия. Обновите его перед сохранением.");
        }
        validate(request);
        var oldReferences = references.findByReportIn_IdOrderById(id);
        var oldFields = fields.findByReportIn_IdOrderById(id);
        writeChildren(report, request, oldReferences, oldFields);
        reportMapper.applyRequest(report, request.report());
        reports.saveAndFlush(report);
        return snapshot(report);
    }

    private void writeChildren(MdReportInEntity report, ReportTemplateRequest request,
                               List<MdReportInReferenceEntity> oldReferences,
                               List<MdReportInReferenceItemEntity> oldFields) {
        var refsById = oldReferences.stream().collect(Collectors.toMap(MdReportInReferenceEntity::getId, Function.identity()));
        var fieldsById = oldFields.stream().collect(Collectors.toMap(MdReportInReferenceItemEntity::getId, Function.identity()));
        for (var ref : request.references()) {
            if (ref.id() == null) continue;
            var existing = refsById.get(ref.id());
            if (existing == null) throw invalid("Справочник отчёта не принадлежит редактируемому отчёту.");
            if (!existing.getReference().getId().equals(ref.referenceId())) {
                throw invalid("Для замены справочника удалите прежний элемент и добавьте новый.");
            }
        }
        for (var field : request.fields()) {
            if (field.id() == null) continue;
            var existing = fieldsById.get(field.id());
            if (existing == null) throw invalid("Поле не принадлежит редактируемому отчёту.");
            if (!existing.getReferenceItem().getId().equals(field.referenceItemId())) {
                throw invalid("Для замены значения справочника удалите прежнее поле и добавьте новое.");
            }
            if (!existing.getTypeFieldValue().getId().equals(field.typeFieldValueId()) &&
                    dataValues.existsByReportInReferenceItem_Id(field.id())) {
                throw new TemplateConflictException("Нельзя изменить тип поля с уже загруженными данными.");
            }
        }
        Set<Long> retainedFields = request.fields().stream().map(ReportTemplateRequest.FieldInput::id)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> retainedRefs = request.references().stream().map(ReportTemplateRequest.ReferenceInput::id)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        for (var field : oldFields) if (!retainedFields.contains(field.getId())) {
            if (dataValues.existsByReportInReferenceItem_Id(field.getId())) {
                throw new TemplateConflictException("Нельзя удалить поле с уже загруженными данными.");
            }
            fields.delete(field);
        }
        for (var ref : oldReferences) if (!retainedRefs.contains(ref.getId())) {
            if (dataReferences.existsByReportInReference_Id(ref.getId())) {
                throw new TemplateConflictException("Нельзя удалить справочник отчёта с уже загруженными данными.");
            }
            references.delete(ref);
        }
        // Release unique keys of removed rows before inserting replacements.
        fields.flush();
        references.flush();
        for (var ref : request.references()) {
            var entity = ref.id() == null ? new MdReportInReferenceEntity() : refsById.get(ref.id());
            referenceMapper.applyRequest(entity, new MdReportInReferenceRequest(new IdReference(report.getId()),
                    new IdReference(ref.referenceId()), new IdReference(ref.typePositionId()), ref.indexNum(), ref.orderSort()));
            references.save(entity);
        }
        for (var field : request.fields()) {
            var entity = field.id() == null ? new MdReportInReferenceItemEntity() : fieldsById.get(field.id());
            fieldMapper.applyRequest(entity, new MdReportInReferenceItemRequest(new IdReference(report.getId()),
                    new IdReference(field.referenceItemId()), new IdReference(field.typeFieldValueId()),
                    field.indexColumn(), field.orderSort(), field.isRequired()));
            fields.save(entity);
        }
        references.flush();
        fields.flush();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        var report = required(reports.findForUpdate(id), "Входящий отчёт", id);
        var oldFields = fields.findByReportIn_IdOrderById(id);
        var oldRefs = references.findByReportIn_IdOrderById(id);
        if (oldFields.stream().anyMatch(field -> dataValues.existsByReportInReferenceItem_Id(field.getId())) ||
                oldRefs.stream().anyMatch(ref -> dataReferences.existsByReportInReference_Id(ref.getId()))) {
            throw new TemplateConflictException("Нельзя удалить отчёт с уже загруженными данными.");
        }
        fields.deleteAll(oldFields);
        fields.flush();
        references.deleteAll(oldRefs);
        references.flush();
        reports.delete(report);
        reports.flush();
    }

    private ReportTemplate snapshot(MdReportInEntity report) {
        var refs = references.findByReportIn_IdOrderById(report.getId()).stream().map(referenceMapper::toModel).toList();
        var items = fields.findByReportIn_IdOrderById(report.getId()).stream().map(fieldMapper::toModel).toList();
        var data = new ReportTemplate(reportMapper.toModel(report), refs, items, null);
        try {
            String revision = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(data.toString().getBytes(StandardCharsets.UTF_8)));
            return new ReportTemplate(data.report(), refs, items, revision);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private static TemplateValidationException invalid(String message) {
        return new TemplateValidationException(message);
    }

    private static <T> T required(Optional<T> value, String name, Long id) {
        return value.orElseThrow(() -> new ResourceNotFoundException(name, id));
    }
}

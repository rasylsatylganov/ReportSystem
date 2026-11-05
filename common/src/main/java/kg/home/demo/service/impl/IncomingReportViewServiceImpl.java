package kg.home.demo.service.impl;

import kg.home.demo.entity.TbReferenceItemEntity;
import kg.home.demo.entity.TbReferenceItemValueEntity;
import kg.home.demo.exception.InvalidRequestException;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.exception.TemplateConflictException;
import kg.home.demo.mapper.DrEnterpriseMapper;
import kg.home.demo.mapper.DrPeriodMapper;
import kg.home.demo.model.*;
import kg.home.demo.repository.DrEnterpriseRepository;
import kg.home.demo.repository.DrPeriodRepository;
import kg.home.demo.repository.TbReferenceItemRepository;
import kg.home.demo.repository.TbReferenceItemValueRepository;
import kg.home.demo.service.IncomingReportViewService;
import kg.home.demo.service.ReportTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomingReportViewServiceImpl implements IncomingReportViewService {
    private final ReportTemplateService templates;
    private final DrPeriodRepository periods;
    private final DrEnterpriseRepository enterprises;
    private final DrPeriodMapper periodMapper;
    private final DrEnterpriseMapper enterpriseMapper;
    private final TbReferenceItemRepository dataReferences;
    private final TbReferenceItemValueRepository dataValues;

    @Override
    public IncomingReportView get(Long reportId, Long periodId, Long enterpriseId, Pageable pageable) {
        var template = templates.get(reportId);
        var period = periods.findById(periodId)
                .orElseThrow(() -> new ResourceNotFoundException("Период", periodId));
        var enterprise = enterprises.findById(enterpriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Предприятие", enterpriseId));
        if (!period.getTypePeriod().getId().equals(template.report().typePeriod().id())) {
            throw new InvalidRequestException("Период не относится к типу периода выбранного отчёта.");
        }
        if (!enterprise.getTypeEnterprise().getId().equals(template.report().typeEnterprise().id())) {
            throw new InvalidRequestException("Предприятие не относится к типу предприятия выбранного отчёта.");
        }

        var orderedReferences = template.references().stream().sorted(Comparator
                .comparing(MdReportInReference::orderSort, Comparator.nullsLast(Long::compareTo))
                .thenComparing(MdReportInReference::id)).toList();
        var vertical = orderedReferences.stream().filter(ref -> !horizontal(ref)).toList();
        var columns = columns(template, orderedReferences, vertical);
        var verticalIds = vertical.stream().map(MdReportInReference::id).toList();
        var rowPage = dataValues.findViewRows(reportId, periodId, enterpriseId,
                verticalIds.isEmpty() ? List.of(-1L) : verticalIds, pageable).map(Number::longValue);
        var rowNumbers = rowPage.getContent();
        List<IncomingReportView.Row> rows = List.of();
        if (!rowNumbers.isEmpty()) {
            var markers = verticalIds.isEmpty() ? List.<TbReferenceItemEntity>of()
                    : dataReferences.findViewMarkers(periodId, enterpriseId, verticalIds,
                            rowNumbers.getFirst(), rowNumbers.getLast());
            var values = dataValues.findViewValues(reportId, periodId, enterpriseId, rowNumbers);
            rows = rows(template, vertical, rowNumbers, markers, values, columns);
        }
        return new IncomingReportView(template.report(), periodMapper.toModel(period),
                enterpriseMapper.toModel(enterprise), columns,
                new PageResult<>(rows, rowPage.getNumber(), rowPage.getSize(),
                        rowPage.getTotalElements(), rowPage.getTotalPages()));
    }

    private static boolean horizontal(MdReportInReference reference) {
        String name = reference.typePosition().nameTypePosition().strip().toLowerCase(Locale.ROOT);
        if (name.startsWith("горизонт") || name.equals("horizontal")) return true;
        if (name.startsWith("вертик") || name.equals("vertical")) return false;
        throw new InvalidRequestException("Неизвестное расположение справочника: " + name);
    }

    private static List<IncomingReportView.Column> columns(ReportTemplate template,
            List<MdReportInReference> references, List<MdReportInReference> vertical) {

        var result = new ArrayList<IncomingReportView.Column>();
        for (var reference : vertical) {
            result.add(new IncomingReportView.Column(referenceKey(reference.id()), reference.reference().nameReference(),
                    null, null, "STRING", true));
        }
        for (var reference : references) {
            if (!horizontal(reference)) continue;
            template.fields().stream()
                    .filter(field -> field.referenceItem().reference().id().equals(reference.reference().id()))
                    .sorted(Comparator.comparing(MdReportInReferenceItem::orderSort, Comparator.nullsLast(Long::compareTo))
                            .thenComparing(MdReportInReferenceItem::id))
                    .forEach(field -> result.add(new IncomingReportView.Column(fieldKey(field.id()),
                            field.referenceItem().nameReferenceItem(), referenceKey(reference.id()),
                            reference.reference().nameReference(), field.typeFieldValue().codeTypeField(), false)));
        }
        return List.copyOf(result);
    }

    private static List<IncomingReportView.Row> rows(ReportTemplate template, List<MdReportInReference> vertical,
            List<Long> rowNumbers, List<TbReferenceItemEntity> markers, List<TbReferenceItemValueEntity> values,
            List<IncomingReportView.Column> columns) {

        var fields = template.fields().stream().collect(Collectors.toMap(MdReportInReferenceItem::id, Function.identity()));
        Map<Long, Map<String, IncomingReportView.Cell>> valueRows = new HashMap<>();
        for (var value : values) {
            var field = fields.get(value.getReportInReferenceItem().getId());
            if (field == null) throw new TemplateConflictException("Структура отчёта изменилась. Обновите просмотр.");
            var cells = valueRows.computeIfAbsent(value.getIndexRow(), ignored -> new HashMap<>());
            var previous = cells.put(fieldKey(field.id()), new IncomingReportView.Cell(value(value, field), null));
            if (previous != null) {
                throw new TemplateConflictException("В строке " + value.getIndexRow() + " несколько значений поля «" +
                        field.referenceItem().nameReferenceItem() + "». Проверьте загруженные данные.");
            }
        }
        var markerRows = new TreeMap<Long, Map<Long, TbReferenceItemEntity>>();
        for (var marker : markers) {
            var byReference = markerRows.computeIfAbsent(marker.getIndexRow(), ignored -> new HashMap<>());
            if (byReference.put(marker.getReportInReference().getId(), marker) != null) {
                throw new TemplateConflictException("В строке " + marker.getIndexRow() +
                        " несколько значений одного вертикального справочника. Проверьте загруженные данные.");
            }
        }

        Map<String, IncomingReportView.Cell> context = new HashMap<>();
        var pending = markerRows.entrySet().iterator();
        Map.Entry<Long, Map<Long, TbReferenceItemEntity>> next = pending.hasNext() ? pending.next() : null;
        var result = new ArrayList<IncomingReportView.Row>();
        for (Long row : rowNumbers) {
            while (next != null && next.getKey() <= row) {
                applyMarkers(context, vertical, next.getValue());
                next = pending.hasNext() ? pending.next() : null;
            }
            Map<String, IncomingReportView.Cell> cells = new HashMap<>(context);
            cells.putAll(valueRows.getOrDefault(row, Map.of()));
            result.add(new IncomingReportView.Row(row, columns.stream()
                    .map(column -> cells.getOrDefault(column.key(), new IncomingReportView.Cell(null, null))).toList()));
        }
        return List.copyOf(result);
    }

    private static void applyMarkers(Map<String, IncomingReportView.Cell> context,
            List<MdReportInReference> vertical, Map<Long, TbReferenceItemEntity> markers) {

        for (int index = 0; index < vertical.size(); index++) {
            var reference = vertical.get(index);
            var marker = markers.get(reference.id());
            if (marker == null) continue;
            // A new outer group ends all inner groups, even if their last marker precedes this page.
            for (int inner = index + 1; inner < vertical.size(); inner++) {
                context.remove(referenceKey(vertical.get(inner).id()));
            }
            context.put(referenceKey(reference.id()), new IncomingReportView.Cell(
                    marker.getReferenceItem().getNameReferenceItem(), marker.getId().toString()));
        }
    }

    private static String value(TbReferenceItemValueEntity value, MdReportInReferenceItem field) {
        return switch (field.typeFieldValue().codeTypeField()) {
            case "NUMBER" -> value.getValueNumber() == null ? null : value.getValueNumber().stripTrailingZeros().toPlainString();
            case "STRING" -> value.getValueString();
            case "DATETIME" -> value.getValueDatetime() == null ? null : value.getValueDatetime().toString();
            default -> throw new InvalidRequestException("Неизвестный тип данных поля: " + field.typeFieldValue().codeTypeField());
        };
    }

    private static String referenceKey(Long id) { return "reference-" + id; }
    private static String fieldKey(Long id) { return "field-" + id; }
}

package kg.home.demo.service.impl;

import jakarta.persistence.EntityManager;
import kg.home.demo.entity.*;
import kg.home.demo.exception.InvalidRequestException;
import kg.home.demo.exception.TemplateConflictException;
import kg.home.demo.model.IncomingReportImportResult;
import kg.home.demo.repository.*;
import kg.home.demo.service.IncomingReportImportService;
import kg.home.demo.service.ReportMetadataGuard;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class IncomingReportImportServiceImpl implements IncomingReportImportService {
    private static final Pattern FILENAME = Pattern.compile(
            "^([A-Za-z0-9-]{3})_([A-Za-z0-9-]{1,20})_([A-Za-z0-9-]{1,50})_([A-Za-z0-9-]{1,20})\\.xlsx$",
            Pattern.CASE_INSENSITIVE);
    private static final Comparator<MdReportInReferenceEntity> REFERENCE_ORDER = Comparator
            .comparing(MdReportInReferenceEntity::getOrderSort, Comparator.nullsLast(Long::compareTo))
            .thenComparing(MdReportInReferenceEntity::getId);
    private static final Comparator<MdReportInReferenceItemEntity> FIELD_ORDER = Comparator
            .comparing(MdReportInReferenceItemEntity::getOrderSort, Comparator.nullsLast(Long::compareTo))
            .thenComparing(MdReportInReferenceItemEntity::getId);

    private final DrTypePeriodRepository periodTypes;
    private final DrTypeEnterpriseRepository enterpriseTypes;
    private final DrPeriodRepository periods;
    private final DrEnterpriseRepository enterprises;
    private final MdReportInRepository reports;
    private final MdReportInReferenceRepository references;
    private final MdReportInReferenceItemRepository fields;
    private final DrReferenceItemRepository dictionaryItems;
    private final TbReferenceItemRepository dataReferences;
    private final ReportMetadataGuard metadataGuard;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public IncomingReportImportResult importWorkbook(String filename, byte[] content) {
        var name = FILENAME.matcher(filename == null ? "" : filename);
        if (!name.matches()) throw invalid("Имя файла должно иметь формат " +
                "ТипПериода_КодПериода_ТипПредприятия_КодПредприятия.xlsx, например QUA_2026Q02_BANK_1000001.xlsx.");
        if (content == null || content.length == 0) throw invalid("Выбранный файл пуст.");
        if (content.length > MAX_FILE_BYTES) throw invalid("Размер файла не должен превышать 20 МБ.");

        // Serialize imports and metadata writes before resolving any structure or writing cells.
        metadataGuard.lockMetadata();
        var periodType = periodTypes.findByCodeTypePeriodIgnoreCase(name.group(1))
                .orElseThrow(() -> invalid("Не найден тип периода с кодом «" + name.group(1) + "»."));
        var enterpriseType = enterpriseTypes.findByCodeTypeEnterpriseIgnoreCase(name.group(3))
                .orElseThrow(() -> invalid("Не найден тип предприятия с кодом «" + name.group(3) + "»."));
        var period = periods.findByTypePeriod_IdAndCodePeriodIgnoreCase(periodType.getId(), name.group(2))
                .orElseThrow(() -> invalid("Не найден период «" + name.group(2) + "» для типа «" + name.group(1) + "»."));
        var enterprise = enterprises.findByTypeEnterprise_IdAndCodeEnterpriseIgnoreCase(enterpriseType.getId(), name.group(4))
                .orElseThrow(() -> invalid("Не найдено предприятие «" + name.group(4) + "» типа «" + name.group(3) + "»."));
        var context = new ImportContext(period, enterprise, period.getNamePeriod(), enterprise.getNameEnterprise());

        try (var workbook = open(content)) {
            if (workbook.getNumberOfSheets() < 1 || workbook.getNumberOfSheets() > 100) {
                throw invalid("Книга должна содержать от 1 до 100 листов с кодами входящих отчётов.");
            }
            List<SheetPlan> plans = new ArrayList<>();
            Set<Long> reportIds = new HashSet<>();
            Map<Long, Map<String, List<DrReferenceItemEntity>>> dictionaries = new HashMap<>();
            for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
                var sheet = workbook.getSheetAt(index);
                var report = reports.findByTypePeriod_IdAndTypeEnterprise_IdAndCodeReportInIgnoreCase(
                                periodType.getId(), enterpriseType.getId(), sheet.getSheetName())
                        .orElseThrow(() -> invalid("Лист «" + sheet.getSheetName() +
                                "»: не найден код входящего отчёта для пары «" + name.group(1) + " + " + name.group(3) + "»."));
                if (!reportIds.add(report.getId())) throw invalid("В книге повторяется форма «" + report.getCodeReportIn() + "».");
                if (dataReferences.existsByPeriod_IdAndEnterprise_IdAndReportInReference_ReportIn_Id(
                        period.getId(), enterprise.getId(), report.getId())) {
                    throw new TemplateConflictException("Лист «" + sheet.getSheetName() +
                            "»: для этой формы, периода и предприятия уже есть рабочие данные. Повторный импорт запрещён.");
                }
                plans.add(plan(sheet, report, dictionaries));
            }
            List<IncomingReportImportResult.SheetResult> results = new ArrayList<>();
            for (var plan : plans) results.add(importSheet(plan, context));
            entityManager.flush();
            return new IncomingReportImportResult(filename, periodType.getId(), enterpriseType.getId(), period.getId(), context.periodName(),
                    enterprise.getId(), context.enterpriseName(),
                    results.stream().mapToLong(IncomingReportImportResult.SheetResult::rowsImported).sum(),
                    results.stream().mapToLong(IncomingReportImportResult.SheetResult::valuesImported).sum(), List.copyOf(results));
        } catch (IOException exception) {
            throw invalid("Не удалось прочитать книгу Excel. Проверьте файл и повторите загрузку.");
        }
    }

    private SheetPlan plan(XSSFSheet sheet, MdReportInEntity report,
                           Map<Long, Map<String, List<DrReferenceItemEntity>>> dictionaries) {
        String prefix = "Лист «" + sheet.getSheetName() + "»: ";
        if (report.getIndexRowScanBegin() == null || report.getIndexRowScanEnd() == null ||
                report.getIndexRowScanBegin() < 1 || report.getIndexRowScanEnd() > 1048576 ||
                report.getIndexRowScanEnd() < report.getIndexRowScanBegin()) {
            throw invalid(prefix + "некорректный диапазон строк в метаданных.");
        }
        var refs = references.findByReportIn_IdOrderById(report.getId()).stream().sorted(REFERENCE_ORDER).toList();
        var items = fields.findByReportIn_IdOrderById(report.getId());
        if (refs.isEmpty() || items.isEmpty()) throw invalid(prefix + "в метаданных отсутствует структура отчёта.");
        Set<Integer> columns = new HashSet<>();
        List<VerticalPlan> vertical = new ArrayList<>();
        List<FieldPlan> values = new ArrayList<>();
        int firstRow = Math.toIntExact(report.getIndexRowScanBegin()) - 1;
        for (var ref : refs) {
            if (ReportTemplateServiceImpl.isHorizontal(ref.getTypePosition())) {
                if (ref.getIndexNum() == null || ref.getIndexNum() < 1 || ref.getIndexNum() > 1048576) {
                    throw invalid(prefix + "некорректная строка шапки в метаданных.");
                }
                int headerRow = Math.toIntExact(ref.getIndexNum()) - 1;
                firstRow = Math.max(firstRow, headerRow + 1);
                var group = items.stream().filter(item -> item.getReferenceItem().getReference().getId()
                        .equals(ref.getReference().getId())).sorted(FIELD_ORDER).toList();
                if (group.isEmpty()) throw invalid(prefix + "горизонтальная шапка не содержит полей.");
                for (var item : group) {
                    int column = column(item.getIndexColumn(), prefix);
                    if (!columns.add(column)) throw invalid(prefix + "несколько элементов занимают один столбец Excel.");
                    String type = item.getTypeFieldValue().getCodeTypeField();
                    if (!Set.of("NUMBER", "STRING", "DATETIME").contains(type)) {
                        throw invalid(prefix + "неподдерживаемый тип поля «" + type + "».");
                    }
                    values.add(new FieldPlan(item, ref, item.getReferenceItem(), column, headerRow,
                            type, Objects.equals(item.getIsRequired(), 1)));
                }
            } else {
                int column = column(ref.getIndexNum(), prefix);
                if (!columns.add(column)) throw invalid(prefix + "несколько элементов занимают один столбец Excel.");
                vertical.add(new VerticalPlan(ref, column, dictionary(ref.getReference().getId(), dictionaries)));
            }
        }
        if (values.size() != items.size()) throw invalid(prefix + "не все поля связаны с горизонтальными шапками.");
        var cells = new ExcelImportCells(sheet, columns);
        for (var field : values) {
            var item = match(field.reference().getReference().getId(), cells, field.headerRow(), field.column(),
                    dictionary(field.reference().getReference().getId(), dictionaries));
            if (!item.getId().equals(field.item().getId())) {
                throw cells.error(field.headerRow(), field.column(), "Заголовок не соответствует полю «" +
                        field.item().getNameReferenceItem() + "», указанному в метаданных.");
            }
        }
        int leadingColumn = vertical.isEmpty() ? values.getFirst().column() : vertical.getFirst().column();
        int lastRow = Math.min(Math.toIntExact(report.getIndexRowScanEnd()) - 1, sheet.getLastRowNum());
        return new SheetPlan(sheet.getSheetName(), report.getId(), cells, vertical, values, leadingColumn, firstRow, lastRow);
    }

    private IncomingReportImportResult.SheetResult importSheet(SheetPlan plan, ImportContext context) {
        Map<Long, TbReferenceItemEntity> headers = new HashMap<>();
        for (var field : plan.fields()) {
            headers.put(field.metadata().getId(), marker(context, field.reference(), field.item(), field.headerRow() + 1));
        }
        Map<Long, Integer> groupOrigins = new HashMap<>();
        long rowCount = 0;
        long valueCount = 0;
        int lastDataRow = plan.firstRow() - 1;
        for (int row = plan.firstRow(); row <= plan.lastRow(); row++) {
            var leading = plan.cells().cell(row, plan.leadingColumn());
            if (plan.cells().blank(leading)) break;
            for (var dimension : plan.vertical()) {
                Cell cell = plan.cells().cell(row, dimension.column());
                if (plan.cells().blank(cell)) throw plan.cells().error(row, dimension.column(), "Не заполнено значение справочника.");
                int origin = cell.getRowIndex();
                Long referenceId = dimension.metadata().getId();
                if (!Objects.equals(groupOrigins.get(referenceId), origin)) {
                    var item = match(dimension.metadata().getReference().getId(), plan.cells(), row,
                            dimension.column(), dimension.items());
                    marker(context, dimension.metadata(), item, row + 1);
                    groupOrigins.put(referenceId, origin);
                }
            }
            for (var field : plan.fields()) {
                Cell cell = plan.cells().cell(row, field.column());
                if (plan.cells().blank(cell)) {
                    if (field.required()) throw plan.cells().error(row, field.column(), "Не заполнено обязательное поле «" +
                            field.item().getNameReferenceItem() + "».");
                    continue;
                }
                var value = new TbReferenceItemValueEntity();
                value.setReferenceItem(headers.get(field.metadata().getId()));
                value.setReportInReferenceItem(field.metadata());
                value.setIndexRow((long) row + 1);
                switch (field.type()) {
                    case "NUMBER" -> value.setValueNumber(plan.cells().number(cell));
                    case "DATETIME" -> value.setValueDatetime(plan.cells().date(cell));
                    case "STRING" -> {
                        String text = plan.cells().text(cell);
                        if (text.length() > 2000) throw plan.cells().error(row, field.column(), "Текст длиннее 2000 символов.");
                        value.setValueString(text);
                    }
                    default -> throw new IllegalStateException("Unsupported field type");
                }
                entityManager.persist(value);
                valueCount++;
            }
            rowCount++;
            lastDataRow = row;
            if (rowCount % 500 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        if (valueCount == 0) throw invalid("Лист «" + plan.name() + "»: в заданном диапазоне не найдено данных для импорта.");
        return new IncomingReportImportResult.SheetResult(plan.name(), plan.reportId(), rowCount, valueCount,
                plan.firstRow() + 1, lastDataRow + 1);
    }

    private TbReferenceItemEntity marker(ImportContext context, MdReportInReferenceEntity metadata,
                                         DrReferenceItemEntity item, int row) {
        var marker = new TbReferenceItemEntity();
        marker.setPeriod(context.period());
        marker.setEnterprise(context.enterprise());
        marker.setReferenceItem(item);
        marker.setReportInReference(metadata);
        marker.setIndexRow((long) row);
        entityManager.persist(marker);
        return marker;
    }

    private Map<String, List<DrReferenceItemEntity>> dictionary(Long referenceId,
            Map<Long, Map<String, List<DrReferenceItemEntity>>> dictionaries) {
        return dictionaries.computeIfAbsent(referenceId, id -> {
            Map<String, List<DrReferenceItemEntity>> names = new HashMap<>();
            for (var item : dictionaryItems.findByReference_IdOrderById(id)) {
                names.computeIfAbsent(ExcelImportCells.normalized(item.getNameReferenceItem()), ignored -> new ArrayList<>()).add(item);
            }
            return names;
        });
    }

    private static DrReferenceItemEntity match(Long referenceId, ExcelImportCells cells, int row, int column,
                                               Map<String, List<DrReferenceItemEntity>> names) {
        String name = cells.text(cells.cell(row, column));
        var matches = names.getOrDefault(ExcelImportCells.normalized(name), List.of());
        if (matches.isEmpty()) throw cells.error(row, column, "В справочнике ID=" + referenceId +
                " не найдено наименование «" + name + "».");
        if (matches.size() > 1) throw cells.error(row, column, "Наименование «" + name +
                "» неоднозначно в справочнике ID=" + referenceId + ".");
        return matches.getFirst();
    }

    private static int column(Long value, String prefix) {
        if (value == null || value < 1 || value > 16384) throw invalid(prefix + "некорректный номер столбца в метаданных.");
        return Math.toIntExact(value) - 1;
    }

    private static XSSFWorkbook open(byte[] content) {
        try { return new XSSFWorkbook(new ByteArrayInputStream(content)); }
        catch (IOException | RuntimeException exception) {
            throw invalid("Не удалось открыть файл .xlsx. Проверьте, что это исправная книга Excel без пароля.");
        }
    }

    private static InvalidRequestException invalid(String message) {
        return new InvalidRequestException(message);
    }

    private record ImportContext(DrPeriodEntity period, DrEnterpriseEntity enterprise,
                                 String periodName, String enterpriseName) {}
    private record FieldPlan(MdReportInReferenceItemEntity metadata, MdReportInReferenceEntity reference,
                             DrReferenceItemEntity item, int column, int headerRow, String type, boolean required) {}
    private record VerticalPlan(MdReportInReferenceEntity metadata, int column,
                                Map<String, List<DrReferenceItemEntity>> items) {}
    private record SheetPlan(String name, Long reportId, ExcelImportCells cells, List<VerticalPlan> vertical,
                             List<FieldPlan> fields, int leadingColumn, int firstRow, int lastRow) {}
}

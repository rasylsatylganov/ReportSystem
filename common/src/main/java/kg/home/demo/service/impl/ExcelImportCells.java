package kg.home.demo.service.impl;

import kg.home.demo.exception.InvalidRequestException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;

/** Reads saved Excel values; formulas are never executed and external workbooks are never opened. */
final class ExcelImportCells {
    private final XSSFSheet sheet;
    private final DataFormatter formatter = new DataFormatter(Locale.forLanguageTag("ru-RU"));
    private final Map<Integer, List<CellRangeAddress>> merges = new HashMap<>();

    ExcelImportCells(XSSFSheet sheet, Set<Integer> columns) {
        this.sheet = sheet;
        formatter.setUseCachedValuesForFormulaCells(true);
        for (var range : sheet.getMergedRegions()) {
            for (int column : columns) {
                if (column >= range.getFirstColumn() && column <= range.getLastColumn()) {
                    merges.computeIfAbsent(column, ignored -> new ArrayList<>()).add(range);
                }
            }
        }
        merges.values().forEach(ranges -> ranges.sort(Comparator.comparingInt(CellRangeAddress::getFirstRow)));
    }

    Cell cell(int row, int column) {
        // Binary search avoids checking every merged group on every imported row.
        var ranges = merges.getOrDefault(column, List.of());
        int low = 0;
        int high = ranges.size() - 1;
        while (low <= high) {
            int middle = (low + high) >>> 1;
            var range = ranges.get(middle);
            if (row < range.getFirstRow()) high = middle - 1;
            else if (row > range.getLastRow()) low = middle + 1;
            else {
                row = range.getFirstRow();
                column = range.getFirstColumn();
                break;
            }
        }
        var sourceRow = sheet.getRow(row);
        return sourceRow == null ? null : sourceRow.getCell(column);
    }

    boolean blank(Cell cell) {
        CellType type = type(cell);
        return type == CellType.BLANK || (type == CellType.STRING && clean(cell.getStringCellValue()).isEmpty());
    }

    String text(Cell cell) {
        if (blank(cell)) return "";
        return clean(formatter.formatCellValue(cell));
    }

    BigDecimal number(Cell cell) {
        try {
            String value = type(cell) == CellType.NUMERIC ? rawNumber(cell)
                    : text(cell).replace(" ", "").replace("\u00a0", "").replace("\u202f", "").replace(',', '.');
            var number = new BigDecimal(value).setScale(6, RoundingMode.UNNECESSARY);
            if (number.precision() > 28) throw new ArithmeticException("NUMBER(28,6)");
            return number;
        } catch (NumberFormatException | ArithmeticException exception) {
            throw error(cell, "Ожидается число: не более 22 цифр до запятой и 6 после неё.");
        }
    }

    LocalDateTime date(Cell cell) {
        if (type(cell) == CellType.NUMERIC) {
            double value = cell.getNumericCellValue();
            if (!DateUtil.isValidExcelDate(value) || value > 2958465) {
                throw error(cell, "Некорректная дата Excel.");
            }
            return DateUtil.getLocalDateTime(value, sheet.getWorkbook().isDate1904()).withNano(0);
        }
        String value = text(cell);
        for (var format : List.of(DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                strict("dd.MM.uuuu HH:mm:ss"), strict("dd.MM.uuuu HH:mm"))) {
            String candidate = format == DateTimeFormatter.ISO_LOCAL_DATE_TIME ? value.replace(' ', 'T') : value;
            try { return LocalDateTime.parse(candidate, format).withNano(0); }
            catch (DateTimeParseException ignored) { /* Try the next supported format. */ }
        }
        for (var format : List.of(DateTimeFormatter.ISO_LOCAL_DATE, strict("dd.MM.uuuu"))) {
            try { return LocalDate.parse(value, format).atStartOfDay(); }
            catch (DateTimeParseException ignored) { /* Try the next supported format. */ }
        }
        throw error(cell, "Ожидается дата Excel, ДД.ММ.ГГГГ или ГГГГ-ММ-ДД, при необходимости со временем.");
    }

    InvalidRequestException error(int row, int column, String message) {
        return new InvalidRequestException("Лист «" + sheet.getSheetName() + "», ячейка " +
                new CellReference(row, column).formatAsString() + ": " + message);
    }

    private InvalidRequestException error(Cell cell, String message) {
        return error(cell.getRowIndex(), cell.getColumnIndex(), message);
    }

    private CellType type(Cell cell) {
        if (cell == null) return CellType.BLANK;
        CellType type = cell.getCellType();
        if (type == CellType.FORMULA) {
            if (((XSSFCell) cell).getRawValue() == null) {
                throw error(cell, "У формулы нет сохранённого результата. Пересчитайте и сохраните файл в Excel.");
            }
            type = cell.getCachedFormulaResultType();
        }
        if (type == CellType.ERROR) throw error(cell, "Ячейка содержит ошибку Excel.");
        if (type == CellType.BOOLEAN) throw error(cell, "Логическое значение не поддерживается метаданными отчёта.");
        return type;
    }

    private static String rawNumber(Cell cell) {
        return ((XSSFCell) cell).getRawValue();
    }

    private static DateTimeFormatter strict(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }

    static String clean(String value) {
        return value.replace('\u00a0', ' ').strip();
    }

    static String normalized(String value) {
        return clean(value).toLowerCase(Locale.ROOT);
    }
}

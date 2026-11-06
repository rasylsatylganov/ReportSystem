package kg.home.demo.model;

import java.util.List;

public record IncomingReportImportResult(
        String filename,
        Long typePeriodId,
        Long typeEnterpriseId,
        Long periodId,
        String periodName,
        Long enterpriseId,
        String enterpriseName,
        long totalRows,
        long totalValues,
        List<SheetResult> sheets
) {
    public record SheetResult(String sheetName, Long reportId, long rowsImported, long valuesImported,
                              int firstDataRow, int lastDataRow) {}
}

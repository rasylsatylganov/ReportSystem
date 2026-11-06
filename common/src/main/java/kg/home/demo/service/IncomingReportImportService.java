package kg.home.demo.service;

import kg.home.demo.model.IncomingReportImportResult;

public interface IncomingReportImportService {
    int MAX_FILE_BYTES = 20 * 1024 * 1024;

    IncomingReportImportResult importWorkbook(String filename, byte[] content);
}

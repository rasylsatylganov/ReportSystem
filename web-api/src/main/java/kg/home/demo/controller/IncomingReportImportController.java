package kg.home.demo.controller;

import kg.home.demo.controller.api.IncomingReportImportControllerApi;
import kg.home.demo.exception.InvalidRequestException;
import kg.home.demo.model.IncomingReportImportResult;
import kg.home.demo.service.IncomingReportImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class IncomingReportImportController implements IncomingReportImportControllerApi {
    private final IncomingReportImportService service;

    @Override
    public ResponseEntity<IncomingReportImportResult> upload(MultipartFile file) {
        if (file.getSize() > IncomingReportImportService.MAX_FILE_BYTES) {
            throw new org.springframework.web.multipart.MaxUploadSizeExceededException(IncomingReportImportService.MAX_FILE_BYTES);
        }
        try {
            return ResponseEntity.ok(service.importWorkbook(file.getOriginalFilename(), file.getBytes()));
        } catch (IOException exception) {
            throw new InvalidRequestException("Не удалось прочитать загруженный файл. Повторите загрузку.");
        }
    }
}

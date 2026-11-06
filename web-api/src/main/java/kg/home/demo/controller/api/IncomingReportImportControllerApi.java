package kg.home.demo.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.home.demo.model.ErrorResponse;
import kg.home.demo.model.IncomingReportImportResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Импорт входящих отчётов")
@RequestMapping("/api/incoming-reports")
public interface IncomingReportImportControllerApi {
    @Operation(summary = "Импортировать книгу Excel по метаданным", operationId = "IncomingReport_import",
            description = "Имя: ТипПериода_КодПериода_ТипПредприятия_КодПредприятия.xlsx. " +
                    "Имя листа = CODE_REPORT_IN. Максимум 20 МБ. Весь файл сохраняется одной транзакцией; " +
                    "повторная загрузка существующей формы за период и предприятие запрещена.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Все листы импортированы"),
            @ApiResponse(responseCode = "400", description = "Ошибка файла, справочников, метаданных или ячейки",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Рабочие данные формы уже существуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "413", description = "Превышен размер файла",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<IncomingReportImportResult> upload(@RequestPart("file") MultipartFile file);
}

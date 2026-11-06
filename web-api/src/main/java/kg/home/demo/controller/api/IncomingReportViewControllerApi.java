package kg.home.demo.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kg.home.demo.model.ErrorResponse;
import kg.home.demo.model.IncomingReportView;
import kg.home.demo.model.IncomingReportSubmission;
import kg.home.demo.model.PageResult;
import kg.home.demo.model.SubmittedIncomingReport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Просмотр входящих отчётов")
@RequestMapping("/api/incoming-reports")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Некорректные параметры или несовместимые типы",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Отчёт, период или предприятие не найдены",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Неоднозначные данные отчёта",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public interface IncomingReportViewControllerApi {
    @Operation(summary = "Периоды и предприятия с загруженными отчётами", operationId = "IncomingReportView_submissions",
            description = "Только сочетания с ячейками TB_REFERENCE_ITEM_VALUE. Пустые формы и маркеры шапки не включаются.")
    @GetMapping("/submissions")
    ResponseEntity<PageResult<IncomingReportSubmission>> submissions(
            @RequestParam @Size(min = 1, max = 100) List<@Positive Long> typePeriodIds,
            @RequestParam @Size(min = 1, max = 100) List<@Positive Long> typeEnterpriseIds,
            @RequestParam(required = false) @Size(max = 300) String search,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size);

    @Operation(summary = "Сданные формы предприятия за период", operationId = "IncomingReportView_submittedReports")
    @GetMapping("/submitted-templates")
    ResponseEntity<PageResult<SubmittedIncomingReport>> submittedReports(
            @RequestParam @Positive Long periodId, @RequestParam @Positive Long enterpriseId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size);

    @Operation(summary = "Просмотреть реальные данные входящего отчёта", operationId = "IncomingReportView_get",
            description = "Данные TB_* для одного отчёта, периода и предприятия. " +
                    "Строки выбираются страницами в БД; вертикальные группы продолжаются между страницами.")
    @GetMapping("/{id}")
    ResponseEntity<IncomingReportView> get(@PathVariable("id") @Positive Long id,
            @RequestParam @Positive Long periodId, @RequestParam @Positive Long enterpriseId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size);
}

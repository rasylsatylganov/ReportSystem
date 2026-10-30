package kg.home.demo.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import kg.home.demo.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Конструктор входящих отчётов")
@RequestMapping("/api/incoming-report-templates")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Некорректная структура или параметры",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Отчёт или связанный объект не найден",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Конфликт данных или устаревшая редакция",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public interface ReportTemplateControllerApi {
    @Operation(summary = "Получить страницу входящих отчётов", operationId = "ReportTemplate_list")
    @GetMapping
    ResponseEntity<PageResult<MdReportIn>> list(@RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size);

    @Operation(summary = "Получить всю структуру отчёта", operationId = "ReportTemplate_get")
    @GetMapping("/{id}")
    ResponseEntity<ReportTemplate> get(@PathVariable("id") @Positive Long id);

    @Operation(summary = "Получить расположения и типы полей", operationId = "ReportTemplate_lookups")
    @GetMapping("/lookups")
    ResponseEntity<ReportDesignerLookups> lookups();

    @Operation(summary = "Проверить структуру без сохранения", operationId = "ReportTemplate_validate")
    @PostMapping("/validate")
    ResponseEntity<Void> validate(@RequestBody @Valid ReportTemplateRequest request);

    @Operation(summary = "Создать отчёт и его структуру одной транзакцией", operationId = "ReportTemplate_create")
    @PostMapping
    ResponseEntity<ReportTemplate> create(@RequestBody @Valid ReportTemplateRequest request);

    @Operation(summary = "Сохранить структуру с проверкой редакции", operationId = "ReportTemplate_update",
            description = "Существующие ID сохраняются. revision из GET обязателен. " +
                    "Удаление используемых метаданных и смена типа поля с данными запрещены.")
    @PutMapping("/{id}")
    ResponseEntity<ReportTemplate> update(@PathVariable("id") @Positive Long id,
            @RequestBody @Valid ReportTemplateRequest request);

    @Operation(summary = "Удалить отчёт без загруженных данных", operationId = "ReportTemplate_delete")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") @Positive Long id);
}

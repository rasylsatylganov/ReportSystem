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
        @ApiResponse(responseCode = "409", description = "Пара типов доступна только для просмотра, конфликт данных или устаревшая редакция",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public interface ReportTemplateControllerApi {
    @Operation(summary = "Получить страницу входящих отчётов", operationId = "ReportTemplate_list",
            description = "Необязательные typePeriodId и typeEnterpriseId фильтруют по одному типу. " +
                    "При передаче обоих условий они применяются совместно до пагинации.")
    @GetMapping
    ResponseEntity<PageResult<MdReportIn>> list(@RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) @Positive Long typePeriodId,
            @RequestParam(required = false) @Positive Long typeEnterpriseId);

    @Operation(summary = "Получить всю структуру отчёта", operationId = "ReportTemplate_get",
            description = "Возвращает readOnly и readOnlyReason. Просмотр доступен и для защищённой пары типов.")
    @GetMapping("/{id}")
    ResponseEntity<ReportTemplate> get(@PathVariable("id") @Positive Long id);

    @Operation(summary = "Проверить доступ к метаданным пары типов", operationId = "ReportTemplate_metadataAccess",
            description = "Если в паре есть сданные данные за любой период, все её формы доступны только для просмотра.")
    @GetMapping("/metadata-access")
    ResponseEntity<ReportMetadataAccess> metadataAccess(@RequestParam @Positive Long typePeriodId,
            @RequestParam @Positive Long typeEnterpriseId);

    @Operation(summary = "Получить расположения и типы полей", operationId = "ReportTemplate_lookups")
    @GetMapping("/lookups")
    ResponseEntity<ReportDesignerLookups> lookups();

    @Operation(summary = "Проверить структуру без сохранения", operationId = "ReportTemplate_validate")
    @PostMapping("/validate")
    ResponseEntity<Void> validate(@RequestBody @Valid ReportTemplateRequest request);

    @Operation(summary = "Создать отчёт и его структуру одной транзакцией", operationId = "ReportTemplate_create",
            description = "Создание запрещено, если в целевой паре типов уже есть сданные отчёты (409).")
    @PostMapping
    ResponseEntity<ReportTemplate> create(@RequestBody @Valid ReportTemplateRequest request);

    @Operation(summary = "Сохранить структуру с проверкой редакции", operationId = "ReportTemplate_update",
            description = "Существующие ID сохраняются. revision из GET обязателен. " +
                    "Если в исходной или целевой паре типов есть сданные данные за любой период, изменение запрещено (409).")
    @PutMapping("/{id}")
    ResponseEntity<ReportTemplate> update(@PathVariable("id") @Positive Long id,
            @RequestBody @Valid ReportTemplateRequest request);

    @Operation(summary = "Удалить отчёт без загруженных данных", operationId = "ReportTemplate_delete",
            description = "Удаление запрещено для всех форм пары типов, в которой есть сданные данные (409).")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") @Positive Long id);
}

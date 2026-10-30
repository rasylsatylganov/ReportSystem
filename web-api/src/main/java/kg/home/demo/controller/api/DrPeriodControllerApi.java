package kg.home.demo.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import kg.home.demo.model.DrPeriod;
import kg.home.demo.model.DrPeriodRequest;
import kg.home.demo.model.ErrorResponse;
import kg.home.demo.model.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Периоды")
@RequestMapping("/api/dr-periods")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Некорректные параметры или тело запроса",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
})
public interface DrPeriodControllerApi {

    @Operation(summary = "Получить страницу записей",
            operationId = "DrPeriod_findAllDrPeriods",
            description = "Страницы от 0, размер 1–100, по умолчанию 20. Сортировка по sortIndex, затем id. " +
                    "Необязательный typePeriodId фильтрует по типу периода.")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @GetMapping
    ResponseEntity<PageResult<DrPeriod>> findAllDrPeriods(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) @Positive Long typePeriodId);

    @Operation(summary = "Получить запись по ID",
            operationId = "DrPeriod_findDrPeriodById")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    ResponseEntity<DrPeriod> findDrPeriodById(@PathVariable("id") @Positive Long id);

    @Operation(summary = "Создать запись",
            operationId = "DrPeriod_createDrPeriod")
    @ApiResponse(responseCode = "201", description = "Запись создана", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    ResponseEntity<DrPeriod> createDrPeriod(@Valid @RequestBody DrPeriodRequest request);

    @Operation(summary = "Обновить запись",
            operationId = "DrPeriod_updateDrPeriod")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    ResponseEntity<DrPeriod> updateDrPeriod(
            @PathVariable("id") @Positive Long id, @Valid @RequestBody DrPeriodRequest request);

    @Operation(summary = "Удалить запись",
            operationId = "DrPeriod_deleteDrPeriod")
    @ApiResponse(responseCode = "204", description = "Операция выполнена, тело ответа отсутствует", content = @Content)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteDrPeriod(@PathVariable("id") @Positive Long id);

    @Operation(summary = "Переместить период вверх", operationId = "DrPeriod_moveUp",
            description = "Обмен порядком с предыдущим периодом, в том числе через границу страниц. " +
                    "typePeriodId ограничивает соседей выбранным типом. На границе списка — без изменений.")
    @ApiResponse(responseCode = "204", description = "Операция выполнена", content = @Content)
    @ApiResponse(responseCode = "404", description = "Период не найден в выбранном списке",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/move-up")
    ResponseEntity<Void> moveUp(@PathVariable("id") @Positive Long id,
            @RequestParam(required = false) @Positive Long typePeriodId);

    @Operation(summary = "Переместить период вниз", operationId = "DrPeriod_moveDown",
            description = "Обмен порядком со следующим периодом, в том числе через границу страниц. " +
                    "typePeriodId ограничивает соседей выбранным типом. На границе списка — без изменений.")
    @ApiResponse(responseCode = "204", description = "Операция выполнена", content = @Content)
    @ApiResponse(responseCode = "404", description = "Период не найден в выбранном списке",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/move-down")
    ResponseEntity<Void> moveDown(@PathVariable("id") @Positive Long id,
            @RequestParam(required = false) @Positive Long typePeriodId);
}

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
import kg.home.demo.model.DrReferenceItem;
import kg.home.demo.model.DrReferenceItemRequest;
import kg.home.demo.model.ErrorResponse;
import kg.home.demo.model.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Значения справочников")
@RequestMapping("/api/dr-reference-items")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Некорректные параметры или тело запроса",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
})
public interface DrReferenceItemControllerApi {

    @Operation(summary = "Получить страницу записей",
            operationId = "DrReferenceItem_findAllDrReferenceItems",
            description = "Страницы от 0, размер 1–100, по умолчанию 20. Сортировка по sortIndex, затем id. " +
                    "Необязательный referenceId фильтрует по справочнику.")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @GetMapping
    ResponseEntity<PageResult<DrReferenceItem>> findAllDrReferenceItems(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) @Positive Long referenceId);

    @Operation(summary = "Получить запись по ID",
            operationId = "DrReferenceItem_findDrReferenceItemById")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    ResponseEntity<DrReferenceItem> findDrReferenceItemById(@PathVariable("id") @Positive Long id);

    @Operation(summary = "Создать запись",
            operationId = "DrReferenceItem_createDrReferenceItem")
    @ApiResponse(responseCode = "201", description = "Запись создана", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    ResponseEntity<DrReferenceItem> createDrReferenceItem(@Valid @RequestBody DrReferenceItemRequest request);

    @Operation(summary = "Обновить запись",
            operationId = "DrReferenceItem_updateDrReferenceItem")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    ResponseEntity<DrReferenceItem> updateDrReferenceItem(
            @PathVariable("id") @Positive Long id, @Valid @RequestBody DrReferenceItemRequest request);

    @Operation(summary = "Удалить запись",
            operationId = "DrReferenceItem_deleteDrReferenceItem")
    @ApiResponse(responseCode = "204", description = "Операция выполнена, тело ответа отсутствует", content = @Content)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteDrReferenceItem(@PathVariable("id") @Positive Long id);

    @Operation(summary = "Переместить значение справочника вверх", operationId = "DrReferenceItem_moveUp",
            description = "Обмен порядком с предыдущим значением, в том числе через границу страниц. " +
                    "referenceId ограничивает соседей выбранным справочником. На границе списка — без изменений.")
    @ApiResponse(responseCode = "204", description = "Операция выполнена", content = @Content)
    @ApiResponse(responseCode = "404", description = "Значение справочника не найдено в выбранном списке",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/move-up")
    ResponseEntity<Void> moveUp(@PathVariable("id") @Positive Long id,
            @RequestParam(required = false) @Positive Long referenceId);

    @Operation(summary = "Переместить значение справочника вниз", operationId = "DrReferenceItem_moveDown",
            description = "Обмен порядком со следующим значением, в том числе через границу страниц. " +
                    "referenceId ограничивает соседей выбранным справочником. На границе списка — без изменений.")
    @ApiResponse(responseCode = "204", description = "Операция выполнена", content = @Content)
    @ApiResponse(responseCode = "404", description = "Значение справочника не найдено в выбранном списке",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/move-down")
    ResponseEntity<Void> moveDown(@PathVariable("id") @Positive Long id,
            @RequestParam(required = false) @Positive Long referenceId);
}

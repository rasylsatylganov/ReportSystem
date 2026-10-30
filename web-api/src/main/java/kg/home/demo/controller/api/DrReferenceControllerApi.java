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
import kg.home.demo.model.DrReference;
import kg.home.demo.model.DrReferenceRequest;
import kg.home.demo.model.ErrorResponse;
import kg.home.demo.model.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Справочники")
@RequestMapping("/api/dr-references")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Некорректные параметры или тело запроса",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
})
public interface DrReferenceControllerApi {

    @Operation(summary = "Получить страницу записей",
            operationId = "DrReference_findAllDrReferences",
            description = "Нумерация страниц начинается с 0; размер от 1 до 100, по умолчанию 20. Сортировка по sortIndex, затем id.")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @GetMapping
    ResponseEntity<PageResult<DrReference>> findAllDrReferences(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size);

    @Operation(summary = "Получить запись по ID",
            operationId = "DrReference_findDrReferenceById")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    ResponseEntity<DrReference> findDrReferenceById(@PathVariable("id") @Positive Long id);

    @Operation(summary = "Создать запись",
            operationId = "DrReference_createDrReference")
    @ApiResponse(responseCode = "201", description = "Запись создана", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    ResponseEntity<DrReference> createDrReference(@Valid @RequestBody DrReferenceRequest request);

    @Operation(summary = "Обновить запись",
            operationId = "DrReference_updateDrReference")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    ResponseEntity<DrReference> updateDrReference(
            @PathVariable("id") @Positive Long id, @Valid @RequestBody DrReferenceRequest request);

    @Operation(summary = "Удалить запись",
            operationId = "DrReference_deleteDrReference")
    @ApiResponse(responseCode = "204", description = "Операция выполнена, тело ответа отсутствует", content = @Content)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteDrReference(@PathVariable("id") @Positive Long id);

    @Operation(summary = "Переместить запись вверх",
            operationId = "DrReference_moveUp",
            description = "Перестановка с соседней записью в полном списке, включая границы страниц. " +
                    "На границе списка операция ничего не меняет.")
    @ApiResponse(responseCode = "204", description = "Операция выполнена, тело ответа отсутствует", content = @Content)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/move-up")
    ResponseEntity<Void> moveUp(@PathVariable("id") @Positive Long id);

    @Operation(summary = "Переместить запись вниз",
            operationId = "DrReference_moveDown",
            description = "Перестановка с соседней записью в полном списке, включая границы страниц. " +
                    "На границе списка операция ничего не меняет.")
    @ApiResponse(responseCode = "204", description = "Операция выполнена, тело ответа отсутствует", content = @Content)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/move-down")
    ResponseEntity<Void> moveDown(@PathVariable("id") @Positive Long id);
}

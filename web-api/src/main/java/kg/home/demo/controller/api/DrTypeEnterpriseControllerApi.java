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
import kg.home.demo.model.DrTypeEnterprise;
import kg.home.demo.model.DrTypeEnterpriseRequest;
import kg.home.demo.model.ErrorResponse;
import kg.home.demo.model.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Типы предприятий")
@RequestMapping("/api/dr-type-enterprises")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Некорректные параметры или тело запроса",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
})
public interface DrTypeEnterpriseControllerApi {

    @Operation(summary = "Получить страницу записей",
            operationId = "DrTypeEnterprise_findAllDrTypeEnterprises",
            description = "Нумерация страниц начинается с 0; размер от 1 до 100, по умолчанию 20. Сортировка по id.")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @GetMapping
    ResponseEntity<PageResult<DrTypeEnterprise>> findAllDrTypeEnterprises(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size);

    @Operation(summary = "Получить запись по ID",
            operationId = "DrTypeEnterprise_findDrTypeEnterpriseById")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    ResponseEntity<DrTypeEnterprise> findDrTypeEnterpriseById(@PathVariable("id") @Positive Long id);

    @Operation(summary = "Создать запись",
            operationId = "DrTypeEnterprise_createDrTypeEnterprise")
    @ApiResponse(responseCode = "201", description = "Запись создана", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    ResponseEntity<DrTypeEnterprise> createDrTypeEnterprise(
            @Valid @RequestBody DrTypeEnterpriseRequest request);

    @Operation(summary = "Обновить запись",
            operationId = "DrTypeEnterprise_updateDrTypeEnterprise")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    ResponseEntity<DrTypeEnterprise> updateDrTypeEnterprise(
            @PathVariable("id") @Positive Long id, @Valid @RequestBody DrTypeEnterpriseRequest request);

    @Operation(summary = "Удалить запись",
            operationId = "DrTypeEnterprise_deleteDrTypeEnterprise")
    @ApiResponse(responseCode = "204", description = "Операция выполнена, тело ответа отсутствует", content = @Content)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteDrTypeEnterprise(@PathVariable("id") @Positive Long id);
}

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
import kg.home.demo.model.DrEnterprise;
import kg.home.demo.model.DrEnterpriseRequest;
import kg.home.demo.model.ErrorResponse;
import kg.home.demo.model.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Предприятия")
@RequestMapping("/api/dr-enterprises")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Некорректные параметры или тело запроса",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
})
public interface DrEnterpriseControllerApi {

    @Operation(summary = "Получить страницу записей",
            operationId = "DrEnterprise_findAllDrEnterprises",
            description = "Нумерация страниц начинается с 0; размер от 1 до 100, по умолчанию 20. Сортировка по id.")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @GetMapping
    ResponseEntity<PageResult<DrEnterprise>> findAllDrEnterprises(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size);

    @Operation(summary = "Получить запись по ID",
            operationId = "DrEnterprise_findDrEnterpriseById")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    ResponseEntity<DrEnterprise> findDrEnterpriseById(@PathVariable("id") @Positive Long id);

    @Operation(summary = "Создать запись",
            operationId = "DrEnterprise_createDrEnterprise")
    @ApiResponse(responseCode = "201", description = "Запись создана", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    ResponseEntity<DrEnterprise> createDrEnterprise(@Valid @RequestBody DrEnterpriseRequest request);

    @Operation(summary = "Обновить запись",
            operationId = "DrEnterprise_updateDrEnterprise")
    @ApiResponse(responseCode = "200", description = "Операция выполнена", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    ResponseEntity<DrEnterprise> updateDrEnterprise(
            @PathVariable("id") @Positive Long id, @Valid @RequestBody DrEnterpriseRequest request);

    @Operation(summary = "Удалить запись",
            operationId = "DrEnterprise_deleteDrEnterprise")
    @ApiResponse(responseCode = "204", description = "Операция выполнена, тело ответа отсутствует", content = @Content)
    @ApiResponse(responseCode = "404", description = "Запись или связанный объект не найдены",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Нарушены ограничения данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteDrEnterprise(@PathVariable("id") @Positive Long id);
}

package kg.home.demo.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record ReportTemplateRequest(
        @NotNull @Valid MdReportInRequest report,
        @NotEmpty(message = "Добавьте хотя бы один справочник.") @Size(max = 500)
        List<@NotNull @Valid ReferenceInput> references,
        @NotEmpty(message = "Добавьте хотя бы одно поле значений.") @Size(max = 16384)
        List<@NotNull @Valid FieldInput> fields,
        String revision
) {
    public record ReferenceInput(
            @Positive Long id,
            @NotNull @Positive Long referenceId,
            @NotNull @Positive Long typePositionId,
            @NotNull @Min(1) @Max(1048576) Long indexNum,
            @Min(0) @Max(9999999999L) Long orderSort
    ) {}

    public record FieldInput(
            @Positive Long id,
            @NotNull @Positive Long referenceItemId,
            @NotNull @Positive Long typeFieldValueId,
            @NotNull @Min(1) @Max(16384) Long indexColumn,
            @Min(0) @Max(9999999999L) Long orderSort,
            @NotNull @Min(0) @Max(1) Integer isRequired
    ) {}
}

package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MdReportInReferenceItemRequest(
        @NotNull(message = "Поле «Входящий отчет» обязательно.")
        @Valid
        IdReference reportIn,
        @NotNull(message = "Поле «Элемент справочника» обязательно.")
        @Valid
        IdReference referenceItem,
        @NotNull(message = "Поле «Тип значения» обязательно.")
        @Valid
        IdReference typeFieldValue,
        @NotNull(message = "Поле «Номер столбца» обязательно.")
        @Digits(integer = 10, fraction = 0, message = "Поле «Номер столбца»: не более 10 цифр.")
        @Positive(message = "Поле «Номер столбца» должно быть положительным.")
        Long indexColumn,
        @Digits(integer = 10, fraction = 0, message = "Поле «Порядок сортировки»: не более 10 цифр.")
        Long orderSort,
        @NotNull(message = "Поле «Признак обязательности» обязательно.")
        @Min(value = 0, message = "Признак обязательности должен быть 0 или 1.")
        @Max(value = 1, message = "Признак обязательности должен быть 0 или 1.")
        Integer isRequired
) {

    public MdReportInReferenceItemRequest {
        isRequired = isRequired != null ? isRequired : 1;
    }
}

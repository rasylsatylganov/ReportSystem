package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MdReportInReferenceRequest(
        @NotNull(message = "Поле «Входящий отчет» обязательно.")
        @Valid
        IdReference reportIn,
        @NotNull(message = "Поле «Справочник» обязательно.")
        @Valid
        IdReference reference,
        @NotNull(message = "Поле «Расположение» обязательно.")
        @Valid
        IdReference typePosition,
        @NotNull(message = "Поле «Номер строки или столбца» обязательно.")
        @Digits(integer = 10, fraction = 0, message = "Поле «Номер строки или столбца»: не более 10 цифр.")
        @Positive(message = "Поле «Номер строки или столбца» должно быть положительным.")
        Long indexNum,
        @Digits(integer = 10, fraction = 0, message = "Поле «Порядок сортировки»: не более 10 цифр.")
        Long orderSort
) {
}

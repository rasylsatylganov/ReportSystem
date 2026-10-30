package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TbReferenceItemRequest(
        @NotNull(message = "Поле «Период» обязательно.")
        @Valid
        IdReference period,
        @NotNull(message = "Поле «Предприятие» обязательно.")
        @Valid
        IdReference enterprise,
        @NotNull(message = "Поле «Элемент справочника» обязательно.")
        @Valid
        IdReference referenceItem,
        @NotNull(message = "Поле «Справочник входящего отчета» обязательно.")
        @Valid
        IdReference reportInReference,
        @NotNull(message = "Поле «Номер строки» обязательно.")
        @Digits(integer = 10, fraction = 0, message = "Поле «Номер строки»: не более 10 цифр.")
        @Positive(message = "Поле «Номер строки» должно быть положительным.")
        Long indexRow
) {
}

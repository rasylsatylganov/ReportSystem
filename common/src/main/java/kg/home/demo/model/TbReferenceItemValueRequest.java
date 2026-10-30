package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TbReferenceItemValueRequest(
        @NotNull(message = "Поле «Элемент справочника» обязательно.")
        @Valid
        IdReference referenceItem,
        @NotNull(message = "Поле «Поле входящего отчета» обязательно.")
        @Valid
        IdReference reportInReferenceItem,
        @Digits(integer = 22, fraction = 6, message = "Числовое значение: не более 22 цифр до и 6 после запятой.")
        BigDecimal valueNumber,
        @Size(max = 2000, message = "Поле «Текстовое значение»: не более 2000 символов.")
        String valueString,
        LocalDateTime valueDatetime,
        @NotNull(message = "Поле «Номер строки» обязательно.")
        @Digits(integer = 10, fraction = 0, message = "Поле «Номер строки»: не более 10 цифр.")
        @Positive(message = "Поле «Номер строки» должно быть положительным.")
        Long indexRow
) {
}

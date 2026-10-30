package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrTypePeriodRequest(
        @NotBlank(message = "Поле «Код типа периода» обязательно.")
        @Size(max = 3, message = "Поле «Код типа периода»: не более 3 символов.")
        String codeTypePeriod,
        @NotBlank(message = "Поле «Наименование типа периода» обязательно.")
        @Size(max = 100, message = "Поле «Наименование типа периода»: не более 100 символов.")
        String nameTypePeriod
) {
}

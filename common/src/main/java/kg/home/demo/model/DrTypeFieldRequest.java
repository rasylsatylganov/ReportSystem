package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrTypeFieldRequest(
        @NotBlank(message = "Поле «Код типа поля» обязательно.")
        @Size(max = 20, message = "Поле «Код типа поля»: не более 20 символов.")
        String codeTypeField,
        @NotBlank(message = "Поле «Наименование типа поля» обязательно.")
        @Size(max = 100, message = "Поле «Наименование типа поля»: не более 100 символов.")
        String nameTypeField
) {
}

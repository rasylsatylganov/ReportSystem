package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrReferenceItemRequest(
        @NotNull(message = "Поле «Справочник» обязательно.")
        @Valid
        IdReference reference,
        @NotBlank(message = "Поле «Код элемента справочника» обязательно.")
        @Size(max = 100, message = "Поле «Код элемента справочника»: не более 100 символов.")
        String codeReferenceItem,
        @NotBlank(message = "Поле «Наименование элемента справочника» обязательно.")
        @Size(max = 300, message = "Поле «Наименование элемента справочника»: не более 300 символов.")
        String nameReferenceItem,
        @Digits(integer = 10, fraction = 0, message = "Поле «Порядок сортировки»: не более 10 цифр.")
        Long orderSort
) {
}

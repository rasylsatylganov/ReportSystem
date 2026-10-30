package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrReferenceRequest(
        @NotBlank(message = "Поле «Код справочника» обязательно.")
        @Size(max = 50, message = "Поле «Код справочника»: не более 50 символов.")
        String codeReference,
        @NotBlank(message = "Поле «Наименование справочника» обязательно.")
        @Size(max = 300, message = "Поле «Наименование справочника»: не более 300 символов.")
        String nameReference
) {
}

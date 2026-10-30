package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrTypePositionRequest(
        @NotBlank(message = "Поле «Наименование расположения» обязательно.")
        @Size(max = 50, message = "Поле «Наименование расположения»: не более 50 символов.")
        String nameTypePosition
) {
}

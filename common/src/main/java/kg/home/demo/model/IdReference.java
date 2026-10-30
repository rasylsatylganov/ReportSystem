package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IdReference(
        @NotNull(message = "Укажите идентификатор связанной записи.")
        @Positive(message = "Идентификатор связанной записи должен быть положительным.")
        Long id
) {
}

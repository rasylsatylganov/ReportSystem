package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrEnterpriseRequest(
        @NotNull(message = "Поле «Тип предприятия» обязательно.")
        @Valid
        IdReference typeEnterprise,
        @NotBlank(message = "Поле «Код предприятия» обязательно.")
        @Size(max = 20, message = "Поле «Код предприятия»: не более 20 символов.")
        String codeEnterprise,
        @NotBlank(message = "Поле «Наименование предприятия» обязательно.")
        @Size(max = 300, message = "Поле «Наименование предприятия»: не более 300 символов.")
        String nameEnterprise
) {
}

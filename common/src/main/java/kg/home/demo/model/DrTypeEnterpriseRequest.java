package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrTypeEnterpriseRequest(
        @NotBlank(message = "Поле «Код типа предприятия» обязательно.")
        @Size(max = 50, message = "Поле «Код типа предприятия»: не более 50 символов.")
        String codeTypeEnterprise,
        @NotBlank(message = "Поле «Наименование типа предприятия» обязательно.")
        @Size(max = 200, message = "Поле «Наименование типа предприятия»: не более 200 символов.")
        String nameTypeEnterprise
) {
}

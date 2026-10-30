package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrPeriodRequest(
        @NotNull(message = "Поле «Тип периода» обязательно.")
        @Valid
        IdReference typePeriod,
        @NotBlank(message = "Поле «Код периода» обязательно.")
        @Size(max = 20, message = "Поле «Код периода»: не более 20 символов.")
        String codePeriod,
        @NotBlank(message = "Поле «Наименование периода» обязательно.")
        @Size(max = 200, message = "Поле «Наименование периода»: не более 200 символов.")
        String namePeriod,
        @NotNull(message = "Поле «Дата начала» обязательно.")
        LocalDate dateBegin,
        @NotNull(message = "Поле «Дата окончания» обязательно.")
        LocalDate dateEnd
) {
    @JsonIgnore
    @AssertTrue(message = "Дата окончания не может быть раньше даты начала.")
    public boolean isDateRangeValid() {
        return this.dateBegin == null || this.dateEnd == null || this.dateEnd.compareTo(this.dateBegin) >= 0;
    }
}

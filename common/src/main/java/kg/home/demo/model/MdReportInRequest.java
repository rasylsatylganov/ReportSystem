package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MdReportInRequest(
        @NotBlank(message = "Поле «Код входящего отчета» обязательно.")
        @Size(max = 50, message = "Поле «Код входящего отчета»: не более 50 символов.")
        String codeReportIn,
        @NotBlank(message = "Поле «Наименование входящего отчета» обязательно.")
        @Size(max = 300, message = "Поле «Наименование входящего отчета»: не более 300 символов.")
        String nameReportIn,
        @NotNull(message = "Поле «Тип периода» обязательно.")
        @Valid
        IdReference typePeriod,
        @NotNull(message = "Поле «Тип предприятия» обязательно.")
        @Valid
        IdReference typeEnterprise,
        @NotNull(message = "Поле «Начальная строка импорта» обязательно.")
        @Digits(integer = 10, fraction = 0, message = "Поле «Начальная строка импорта»: не более 10 цифр.")
        @Positive(message = "Поле «Начальная строка импорта» должно быть положительным.")
        Long indexRowScanBegin,
        @NotNull(message = "Поле «Конечная строка импорта» обязательно.")
        @Digits(integer = 10, fraction = 0, message = "Поле «Конечная строка импорта»: не более 10 цифр.")
        @Positive(message = "Поле «Конечная строка импорта» должно быть положительным.")
        Long indexRowScanEnd
) {
    @JsonIgnore
    @AssertTrue(message = "Конечная строка импорта не может быть меньше начальной.")
    public boolean isScanRangeValid() {
        return this.indexRowScanBegin == null || this.indexRowScanEnd == null ||
                this.indexRowScanEnd >= this.indexRowScanBegin;
    }
}

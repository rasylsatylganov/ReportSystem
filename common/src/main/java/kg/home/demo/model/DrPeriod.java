package kg.home.demo.model;

import java.time.LocalDate;

public record DrPeriod(
        Long id,
        DrTypePeriod typePeriod,
        String codePeriod,
        String namePeriod,
        LocalDate dateBegin,
        LocalDate dateEnd
) {
}

package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrPeriodRequest(
        IdReference typePeriod,
        String codePeriod,
        String namePeriod,
        LocalDate dateBegin,
        LocalDate dateEnd
) {
}

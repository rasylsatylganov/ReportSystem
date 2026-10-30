package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrTypePeriodRequest(
        String codeTypePeriod,
        String nameTypePeriod
) {
}

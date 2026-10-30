package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MdReportInReferenceRequest(
        IdReference reportIn,
        IdReference reference,
        IdReference typePosition,
        Long indexNum,
        Long orderSort
) {
}

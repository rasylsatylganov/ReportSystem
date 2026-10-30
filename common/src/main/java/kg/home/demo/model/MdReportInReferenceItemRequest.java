package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MdReportInReferenceItemRequest(
        IdReference reportIn,
        IdReference referenceItem,
        IdReference typeFieldValue,
        Long indexColumn,
        Long orderSort,
        Integer isRequired
) {

    public MdReportInReferenceItemRequest {
        isRequired = isRequired != null ? isRequired : 1;
    }
}

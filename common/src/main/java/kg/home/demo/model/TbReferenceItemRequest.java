package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TbReferenceItemRequest(
        IdReference period,
        IdReference enterprise,
        IdReference referenceItem,
        IdReference reportInReference,
        Long indexRow
) {
}

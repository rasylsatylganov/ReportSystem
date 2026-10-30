package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrReferenceItemRequest(
        IdReference reference,
        String codeReferenceItem,
        String nameReferenceItem,
        Long orderSort
) {
}

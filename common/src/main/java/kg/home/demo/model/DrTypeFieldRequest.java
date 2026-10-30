package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrTypeFieldRequest(
        String codeTypeField,
        String nameTypeField
) {
}

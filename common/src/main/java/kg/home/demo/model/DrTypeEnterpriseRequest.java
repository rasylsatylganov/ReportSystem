package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrTypeEnterpriseRequest(
        String codeTypeEnterprise,
        String nameTypeEnterprise
) {
}

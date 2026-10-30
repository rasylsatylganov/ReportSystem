package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DrEnterpriseRequest(
        IdReference typeEnterprise,
        String codeEnterprise,
        String nameEnterprise
) {
}

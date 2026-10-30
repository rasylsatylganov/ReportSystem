package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MdReportInRequest(
        String codeReportIn,
        String nameReportIn,
        IdReference typePeriod,
        IdReference typeEnterprise,
        Long indexRowScanBegin,
        Long indexRowScanEnd
) {
}

package kg.home.demo.model;

public record MdReportIn(
        Long id,
        String codeReportIn,
        String nameReportIn,
        DrTypePeriod typePeriod,
        DrTypeEnterprise typeEnterprise,
        Long indexRowScanBegin,
        Long indexRowScanEnd
) {
}

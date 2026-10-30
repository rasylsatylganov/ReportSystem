package kg.home.demo.model;

public record TbReferenceItem(
        Long id,
        DrPeriod period,
        DrEnterprise enterprise,
        DrReferenceItem referenceItem,
        MdReportInReference reportInReference,
        Long indexRow
) {
}

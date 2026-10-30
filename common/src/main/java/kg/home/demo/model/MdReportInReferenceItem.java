package kg.home.demo.model;

public record MdReportInReferenceItem(
        Long id,
        MdReportIn reportIn,
        DrReferenceItem referenceItem,
        DrTypeField typeFieldValue,
        Long indexColumn,
        Long orderSort,
        Integer isRequired
) {
}

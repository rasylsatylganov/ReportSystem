package kg.home.demo.model;

public record MdReportInReference(
        Long id,
        MdReportIn reportIn,
        DrReference reference,
        DrTypePosition typePosition,
        Long indexNum,
        Long orderSort
) {
}

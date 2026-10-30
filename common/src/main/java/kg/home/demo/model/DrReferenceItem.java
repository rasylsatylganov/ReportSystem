package kg.home.demo.model;

public record DrReferenceItem(
        Long id,
        DrReference reference,
        String codeReferenceItem,
        String nameReferenceItem,
        Long sortIndex
) {
}

package kg.home.demo.model;

public record DrEnterprise(
        Long id,
        DrTypeEnterprise typeEnterprise,
        String codeEnterprise,
        String nameEnterprise,
        Long sortIndex
) {
}

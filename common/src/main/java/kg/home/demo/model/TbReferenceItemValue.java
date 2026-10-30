package kg.home.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TbReferenceItemValue(
        Long id,
        TbReferenceItem referenceItem,
        MdReportInReferenceItem reportInReferenceItem,
        BigDecimal valueNumber,
        String valueString,
        LocalDateTime valueDatetime,
        Long indexRow
) {
}

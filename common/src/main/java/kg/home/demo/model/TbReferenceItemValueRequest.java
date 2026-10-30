package kg.home.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TbReferenceItemValueRequest(
        IdReference referenceItem,
        IdReference reportInReferenceItem,
        BigDecimal valueNumber,
        String valueString,
        LocalDateTime valueDatetime,
        Long indexRow
) {
}

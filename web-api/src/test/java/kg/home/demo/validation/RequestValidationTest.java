package kg.home.demo.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import kg.home.demo.model.DrEnterpriseRequest;
import kg.home.demo.model.DrPeriodRequest;
import kg.home.demo.model.DrReferenceItemRequest;
import kg.home.demo.model.DrReferenceRequest;
import kg.home.demo.model.DrTypeEnterpriseRequest;
import kg.home.demo.model.DrTypeFieldRequest;
import kg.home.demo.model.DrTypePeriodRequest;
import kg.home.demo.model.DrTypePositionRequest;
import kg.home.demo.model.IdReference;
import kg.home.demo.model.MdReportInReferenceItemRequest;
import kg.home.demo.model.MdReportInReferenceRequest;
import kg.home.demo.model.MdReportInRequest;
import kg.home.demo.model.TbReferenceItemRequest;
import kg.home.demo.model.TbReferenceItemValueRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestValidationTest {


    private static ValidatorFactory validatorFactory;
    private static Validator validator;


    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    @BeforeAll
    static void initialize() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @ParameterizedTest
    @MethodSource("validRequests")
    void acceptsValidRequest(Object request) {
        assertTrue(validator.validate(request).isEmpty(), () -> validator.validate(request).toString());
    }

    @ParameterizedTest
    @MethodSource("validRequests")
    void rejectsMissingRequiredFields(Object request) throws Exception {
        Object emptyRequest = this.objectMapper.readValue("{}", request.getClass());

        assertFalse(validator.validate(emptyRequest).isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void validatesNestedReference(Long id) {
        DrPeriodRequest request = new DrPeriodRequest(new IdReference(id), "2026M01", "Январь",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertTrue(validator.validate(request).stream()
                .anyMatch(violation -> "typePeriod.id".equals(violation.getPropertyPath().toString())));
    }

    @Test
    void rejectsReversedDates() {
        DrPeriodRequest request = new DrPeriodRequest(new IdReference(1L), "2026M01", "Январь",
                LocalDate.of(2026, 1, 31), LocalDate.of(2026, 1, 1));

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsReversedImportRows() {
        MdReportInRequest request = new MdReportInRequest("R01", "Отчет", new IdReference(1L),
                new IdReference(1L), 10L, 2L);

        assertFalse(validator.validate(request).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"10000000000000000000000", "0.0000001"})
    void rejectsNumbersOutsideDatabasePrecision(String number) {
        TbReferenceItemValueRequest request = new TbReferenceItemValueRequest(
                new IdReference(1L), new IdReference(2L), new BigDecimal(number), null, null, 1L);

        assertFalse(validator.validate(request).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 2})
    void rejectsUnknownRequiredFlag(Integer isRequired) {
        MdReportInReferenceItemRequest request = new MdReportInReferenceItemRequest(
                new IdReference(1L), new IdReference(2L), new IdReference(3L), 1L, null, isRequired);

        assertFalse(validator.validate(request).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "LONG"})
    void rejectsBlankOrLongPeriodCode(String code) {
        assertFalse(validator.validate(new DrTypePeriodRequest(code, "Месячный")).isEmpty());
    }

    static List<Object> validRequests() {
        IdReference reference = new IdReference(1L);
        return List.of(
                new DrEnterpriseRequest(reference, "1000001", "Предприятие"),
                new DrPeriodRequest(reference, "2026M01", "Январь",
                        LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)),
                new DrReferenceItemRequest(reference, "KG", "Кыргызстан"),
                new DrReferenceRequest("country", "Страны"),
                new DrTypeEnterpriseRequest("banks", "Банки"),
                new DrTypeFieldRequest("NUMBER", "Число"),
                new DrTypePeriodRequest("MON", "Месячный"),
                new DrTypePositionRequest("Горизонтально"),
                new MdReportInRequest("R01", "Отчет", reference, reference, 2L, 100L),
                new MdReportInReferenceRequest(reference, reference, reference, 1L, null),
                new MdReportInReferenceItemRequest(reference, reference, reference, 1L, null, null),
                new TbReferenceItemRequest(reference, reference, reference, reference, 1L),
                new TbReferenceItemValueRequest(reference, reference, new BigDecimal("10.5"), null, null, 1L)
        );
    }
}

package kg.home.demo.controller;

import kg.home.demo.exception.GlobalExceptionHandler;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrPeriodRequest;
import kg.home.demo.model.DrTypePeriod;
import kg.home.demo.model.DrTypePeriodRequest;
import kg.home.demo.service.DrEnterpriseService;
import kg.home.demo.service.DrPeriodService;
import kg.home.demo.service.DrReferenceItemService;
import kg.home.demo.service.DrReferenceService;
import kg.home.demo.service.DrTypeEnterpriseService;
import kg.home.demo.service.DrTypePeriodService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiErrorHandlingTest {


    private static final String TYPE_PERIOD_JSON = """
            {"codeTypePeriod":"MON","nameTypePeriod":"Месячный"}
            """;
    private static final String PERIOD_JSON = """
            {"typePeriod":{"id":1},"codePeriod":"2026M01","namePeriod":"Январь",
             "dateBegin":"2026-01-01","dateEnd":"2026-01-31"}
            """;

    private final DrTypePeriodService typePeriodService = mock(DrTypePeriodService.class);
    private final DrPeriodService periodService = mock(DrPeriodService.class);
    private final DrEnterpriseService enterpriseService = mock(DrEnterpriseService.class);
    private final DrReferenceService referenceService = mock(DrReferenceService.class);
    private final DrReferenceItemService referenceItemService = mock(DrReferenceItemService.class);
    private final DrTypeEnterpriseService typeEnterpriseService = mock(DrTypeEnterpriseService.class);
    private LocalValidatorFactoryBean validator;
    private MockMvc mockMvc;

    @BeforeEach
    void initialize() {
        this.validator = new LocalValidatorFactoryBean();
        this.validator.afterPropertiesSet();
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                        new DrTypePeriodController(this.typePeriodService),
                        new DrPeriodController(this.periodService),
                        new DrEnterpriseController(this.enterpriseService),
                        new DrReferenceController(this.referenceService),
                        new DrReferenceItemController(this.referenceItemService),
                        new DrTypeEnterpriseController(this.typeEnterpriseService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(this.validator)
                .build();
    }

    @AfterEach
    void closeValidator() {
        this.validator.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"dr-type-periods", "dr-periods", "dr-enterprises", "dr-references",
            "dr-reference-items", "dr-type-enterprises"})
    void missingRecordsHaveUniformErrorBody(String resource) throws Exception {
        this.assertError(this.mockMvc.perform(get("/api/" + resource + "/99")), 404, "RESOURCE_NOT_FOUND");
    }

    @ParameterizedTest
    @ValueSource(strings = {"dr-type-periods", "dr-periods", "dr-enterprises", "dr-references",
            "dr-reference-items", "dr-type-enterprises"})
    void invalidCreateAndUpdateNeverReachServices(String resource) throws Exception {
        for (HttpMethod method : new HttpMethod[]{HttpMethod.POST, HttpMethod.PUT}) {
            String url = "/api/" + resource + (method == HttpMethod.PUT ? "/1" : "");
            this.assertError(this.mockMvc.perform(request(method, url)
                    .contentType(MediaType.APPLICATION_JSON).content("{}")), 400, "VALIDATION_ERROR");
        }
        verifyNoInteractions(this.typePeriodService, this.periodService, this.enterpriseService,
                this.referenceService, this.referenceItemService, this.typeEnterpriseService);
    }

    @Test
    void validatesNestedReference() throws Exception {
        this.assertError(this.mockMvc.perform(post("/api/dr-periods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(PERIOD_JSON.replace("\"id\":1", "\"id\":0"))), 400, "VALIDATION_ERROR");
        verifyNoInteractions(this.periodService);
    }

    @Test
    void malformedDateHasSafeErrorBody() throws Exception {
        this.assertError(this.mockMvc.perform(post("/api/dr-periods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(PERIOD_JSON.replace("2026-01-01", "not-a-date"))), 400, "MALFORMED_REQUEST");
    }

    @Test
    void malformedJsonHasSafeErrorBody() throws Exception {
        this.assertError(this.mockMvc.perform(post("/api/dr-type-periods")
                .contentType(MediaType.APPLICATION_JSON).content("{")), 400, "MALFORMED_REQUEST");
    }

    @Test
    void conflictsAre409WithoutDatabaseDetails() throws Exception {
        when(this.typePeriodService.createDrTypePeriod(any(DrTypePeriodRequest.class)))
                .thenThrow(new DataIntegrityViolationException("ORA-00001: PRIVATE_SCHEMA.UQ_SECRET"));

        this.assertError(this.mockMvc.perform(post("/api/dr-type-periods")
                        .contentType(MediaType.APPLICATION_JSON).content(TYPE_PERIOD_JSON)), 409, "DATA_CONFLICT")
                .andExpect(content().string(not(containsString("PRIVATE_SCHEMA"))));
    }

    @Test
    void unexpectedUpdateFailureIs500InsteadOf404() throws Exception {
        when(this.periodService.updateDrPeriod(eq(1L), any(DrPeriodRequest.class)))
                .thenThrow(new RuntimeException("Unexpected SQL failure in PRIVATE_TABLE"));

        this.assertError(this.mockMvc.perform(put("/api/dr-periods/1")
                        .contentType(MediaType.APPLICATION_JSON).content(PERIOD_JSON)), 500, "INTERNAL_ERROR")
                .andExpect(content().string(not(containsString("PRIVATE_TABLE"))));
    }

    @Test
    void domainNotFoundUses404() throws Exception {
        when(this.periodService.updateDrPeriod(eq(1L), any(DrPeriodRequest.class)))
                .thenThrow(new ResourceNotFoundException("Период", 1L));

        this.assertError(this.mockMvc.perform(put("/api/dr-periods/1")
                .contentType(MediaType.APPLICATION_JSON).content(PERIOD_JSON)), 404, "RESOURCE_NOT_FOUND");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void invalidPathIdIs400(String id) throws Exception {
        this.assertError(this.mockMvc.perform(get("/api/dr-periods/" + id)), 400, "VALIDATION_ERROR");
        verifyNoInteractions(this.periodService);
    }

    @Test
    void nonNumericIdIs400() throws Exception {
        this.assertError(this.mockMvc.perform(get("/api/dr-periods/abc")), 400, "INVALID_REQUEST");
    }

    @Test
    void unsupportedHttpMethodIs405() throws Exception {
        this.assertError(this.mockMvc.perform(patch("/api/dr-type-periods/1")
                .contentType(MediaType.APPLICATION_JSON).content(TYPE_PERIOD_JSON)), 405, "METHOD_NOT_ALLOWED");
    }

    @Test
    void unsupportedContentTypeIs415() throws Exception {
        this.assertError(this.mockMvc.perform(post("/api/dr-type-periods")
                .contentType(MediaType.TEXT_PLAIN).content(TYPE_PERIOD_JSON)), 415, "UNSUPPORTED_MEDIA_TYPE");
    }

    @Test
    void unknownRouteIs404() throws Exception {
        this.assertError(this.mockMvc.perform(get("/api/unknown")), 404, "RESOURCE_NOT_FOUND");
    }

    @Test
    void successfulCreateKeepsExistingContract() throws Exception {
        when(this.typePeriodService.createDrTypePeriod(any(DrTypePeriodRequest.class)))
                .thenReturn(new DrTypePeriod(7L, "MON", "Месячный", 50L));

        this.mockMvc.perform(post("/api/dr-type-periods")
                        .contentType(MediaType.APPLICATION_JSON).content(TYPE_PERIOD_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.sortIndex").value(50))
                .andExpect(jsonPath("$.nameTypePeriod").value("Месячный"));
    }

    @Test
    void successfulDeleteKeepsNoContentResponse() throws Exception {
        this.mockMvc.perform(delete("/api/dr-type-periods/7"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    private ResultActions assertError(ResultActions result, int status, String code) throws Exception {
        return result.andExpect(status().is(status))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist());
    }
}

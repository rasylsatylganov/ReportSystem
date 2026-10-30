package kg.home.demo.controller;

import kg.home.demo.exception.GlobalExceptionHandler;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrTypeEnterprise;
import kg.home.demo.model.DrTypeEnterpriseRequest;
import kg.home.demo.service.DrTypeEnterpriseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DrTypeEnterpriseControllerTest {

    private final DrTypeEnterpriseService service = mock(DrTypeEnterpriseService.class);
    private final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    private MockMvc mvc;

    @BeforeEach
    void initialize() {
        this.validator.afterPropertiesSet();
        this.mvc = MockMvcBuilders.standaloneSetup(new DrTypeEnterpriseController(this.service))
                .setValidator(this.validator).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @AfterEach
    void closeValidator() {
        this.validator.close();
    }

    @Test
    void crudBindsEditableFieldsAndReturnsServerOrder() throws Exception {
        DrTypeEnterpriseRequest request = new DrTypeEnterpriseRequest("BANK", "Банк");
        DrTypeEnterprise model = new DrTypeEnterprise(7L, "BANK", "Банк", 50L);
        when(this.service.createDrTypeEnterprise(request)).thenReturn(model);
        when(this.service.updateDrTypeEnterprise(7L, request)).thenReturn(model);
        when(this.service.findDrTypeEnterpriseById(7L)).thenReturn(Optional.of(model));
        String json = """
                {"id":999,"sortIndex":-1,"codeTypeEnterprise":"BANK","nameTypeEnterprise":"Банк"}
                """;

        this.mvc.perform(post("/api/dr-type-enterprises").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.sortIndex").value(50));
        this.mvc.perform(put("/api/dr-type-enterprises/7").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.sortIndex").value(50));
        this.mvc.perform(get("/api/dr-type-enterprises/7"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.codeTypeEnterprise").value("BANK"));
        this.mvc.perform(delete("/api/dr-type-enterprises/7")).andExpect(status().isNoContent());
        verify(this.service).deleteDrTypeEnterprise(7L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"up", "down"})
    void moveRoutesBindIdAndReturnNoContent(String direction) throws Exception {
        this.mvc.perform(post("/api/dr-type-enterprises/7/move-" + direction)).andExpect(status().isNoContent());
        if (direction.equals("up")) {
            verify(this.service).moveUp(7L);
        } else {
            verify(this.service).moveDown(7L);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"up", "down"})
    void invalidMoveIdNeverReachesService(String direction) throws Exception {
        this.mvc.perform(post("/api/dr-type-enterprises/0/move-" + direction))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        verifyNoInteractions(this.service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"up", "down"})
    void missingMoveRecordReturnsDomainError(String direction) throws Exception {
        ResourceNotFoundException exception = new ResourceNotFoundException("Тип предприятия", 99L);
        if (direction.equals("up")) {
            doThrow(exception).when(this.service).moveUp(99L);
        } else {
            doThrow(exception).when(this.service).moveDown(99L);
        }
        this.mvc.perform(post("/api/dr-type-enterprises/99/move-" + direction))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }
}

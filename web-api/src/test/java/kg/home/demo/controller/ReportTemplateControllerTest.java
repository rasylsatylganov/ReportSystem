package kg.home.demo.controller;

import kg.home.demo.exception.GlobalExceptionHandler;
import kg.home.demo.exception.TemplateConflictException;
import kg.home.demo.model.*;
import kg.home.demo.service.ReportTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReportTemplateControllerTest {
    @Test
    void validatesPaginationNestedStructureCheckboxAndReturnsConflict() throws Exception {
        var service = mock(ReportTemplateService.class);
        try (var validator = new LocalValidatorFactoryBean()) {
            validator.afterPropertiesSet();
            var mvc = MockMvcBuilders.standaloneSetup(new ReportTemplateController(service))
                    .setValidator(validator).setControllerAdvice(new GlobalExceptionHandler()).build();
            mvc.perform(get("/api/incoming-report-templates?size=101")).andExpect(status().isBadRequest());
            mvc.perform(post("/api/incoming-report-templates").contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isBadRequest());
            mvc.perform(get("/api/incoming-report-templates/0")).andExpect(status().isBadRequest());
            String invalidCheckbox = """
                    {"report":{"codeReportIn":"TEST","nameReportIn":"Тест","typePeriod":{"id":1},
                    "typeEnterprise":{"id":1},"indexRowScanBegin":2,"indexRowScanEnd":100},
                    "references":[{"referenceId":1,"typePositionId":1,"indexNum":2}],
                    "fields":[{"referenceItemId":1,"typeFieldValueId":1,"indexColumn":1,"isRequired":2}]}
                    """;
            mvc.perform(post("/api/incoming-report-templates/validate").contentType(MediaType.APPLICATION_JSON)
                    .content(invalidCheckbox)).andExpect(status().isBadRequest());
            verifyNoInteractions(service);
            when(service.findPage(PageRequest.of(0, 20, Sort.by("id"))))
                    .thenReturn(new PageResult<>(List.of(), 0, 20, 0, 0));
            mvc.perform(get("/api/incoming-report-templates")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
            when(service.lookups()).thenReturn(new ReportDesignerLookups(List.of(), List.of()));
            mvc.perform(get("/api/incoming-report-templates/lookups")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.positions").isArray());
            doThrow(new TemplateConflictException("Отчёт используется.")).when(service).delete(1L);
            mvc.perform(delete("/api/incoming-report-templates/1")).andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("DATA_CONFLICT"));
        }
    }
}

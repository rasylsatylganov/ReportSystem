package kg.home.demo.controller;

import kg.home.demo.exception.GlobalExceptionHandler;
import kg.home.demo.model.PageResult;
import kg.home.demo.service.DrPeriodService;
import kg.home.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DrPeriodFilterTest {
    @Test
    void moveEndpointsValidateIdsAndForwardOptionalFilter() throws Exception {
        DrPeriodService service = mock(DrPeriodService.class);
        try (LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean()) {
            validator.afterPropertiesSet();
            MockMvc mvc = MockMvcBuilders.standaloneSetup(new DrPeriodController(service))
                    .setValidator(validator).setControllerAdvice(new GlobalExceptionHandler()).build();
            for (String direction : List.of("up", "down")) {
                mvc.perform(post("/api/dr-periods/0/move-" + direction)).andExpect(status().isBadRequest());
                mvc.perform(post("/api/dr-periods/1/move-" + direction).param("typePeriodId", "0"))
                        .andExpect(status().isBadRequest());
            }
            verifyNoInteractions(service);
            mvc.perform(post("/api/dr-periods/1/move-up")).andExpect(status().isNoContent());
            mvc.perform(post("/api/dr-periods/1/move-down").param("typePeriodId", "7"))
                    .andExpect(status().isNoContent());
            verify(service).moveUp(1L, null);
            verify(service).moveDown(1L, 7L);
            doThrow(new ResourceNotFoundException("Период", 99L)).when(service).moveUp(99L, 7L);
            mvc.perform(post("/api/dr-periods/99/move-up").param("typePeriodId", "7"))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    void validatesAndForwardsTypeFilterWithPagination() throws Exception {
        DrPeriodService service = mock(DrPeriodService.class);
        PageRequest pageable = PageRequest.of(2, 10, Sort.by("sortIndex", "id"));
        when(service.findDrPeriodsByType(7L, pageable))
                .thenReturn(new PageResult<>(List.of(), 2, 10, 21, 3));
        try (LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean()) {
            validator.afterPropertiesSet();
            MockMvc mvc = MockMvcBuilders.standaloneSetup(new DrPeriodController(service))
                    .setValidator(validator).setControllerAdvice(new GlobalExceptionHandler()).build();
            for (String invalid : List.of("0", "-1", "abc")) {
                mvc.perform(get("/api/dr-periods").param("typePeriodId", invalid))
                        .andExpect(status().isBadRequest());
            }
            verifyNoInteractions(service);
            mvc.perform(get("/api/dr-periods").param("typePeriodId", "7")
                            .param("page", "2").param("size", "10"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.page").value(2))
                    .andExpect(jsonPath("$.totalElements").value(21));
            verify(service).findDrPeriodsByType(7L, pageable);
            verifyNoMoreInteractions(service);
        }
    }
}

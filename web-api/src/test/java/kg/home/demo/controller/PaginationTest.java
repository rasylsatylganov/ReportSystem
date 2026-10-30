package kg.home.demo.controller;

import kg.home.demo.exception.GlobalExceptionHandler;
import kg.home.demo.model.PageResult;
import kg.home.demo.service.DrEnterpriseService;
import kg.home.demo.service.DrPeriodService;
import kg.home.demo.service.DrReferenceItemService;
import kg.home.demo.service.DrReferenceService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaginationTest {

    @ParameterizedTest
    @ValueSource(strings = {"DrEnterprise", "DrPeriod", "DrReference", "DrReferenceItem",
            "DrTypeEnterprise", "DrTypePeriod"})
    void validatesAndForwardsPagination(String resource) throws Exception {
        Class<?> serviceType = Class.forName("kg.home.demo.service." + resource + "Service");
        Object service = mock(serviceType);
        Class<?> controllerType = Class.forName("kg.home.demo.controller." + resource + "Controller");
        Object controller = controllerType.getConstructor(serviceType).newInstance(service);
        Method listMethod = List.of(serviceType.getMethods()).stream()
                .filter(method -> method.getName().startsWith("findAll"))
                .findFirst().orElseThrow();
        listMethod.invoke(doAnswer(invocation -> {
            Pageable pageable = invocation.getArgument(0);
            return new PageResult<>(List.of(), pageable.getPageNumber(), pageable.getPageSize(), 0, 0);
        }).when(service), any(Pageable.class));
        String path = switch (resource) {
            case "DrEnterprise" -> "/api/dr-enterprises";
            case "DrPeriod" -> "/api/dr-periods";
            case "DrReference" -> "/api/dr-references";
            case "DrTypeEnterprise" -> "/api/dr-type-enterprises";
            case "DrTypePeriod" -> "/api/dr-type-periods";
            default -> "/api/dr-reference-items";
        };

        try (LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean()) {
            validator.afterPropertiesSet();
            MockMvc mvc = MockMvcBuilders.standaloneSetup(controller)
                    .setValidator(validator).setControllerAdvice(new GlobalExceptionHandler()).build();
            for (String query : List.of("?page=-1", "?size=0", "?size=101", "?page=abc")) {
                mvc.perform(get(path + query)).andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.code").exists());
            }
            verifyNoInteractions(service);
            mvc.perform(get(path)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(20))
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.totalPages").value(0));
            Sort sort = resource.equals("DrTypePeriod") || resource.equals("DrTypeEnterprise") ||
                    resource.equals("DrReference") || resource.equals("DrPeriod") || resource.equals("DrEnterprise") ||
                    resource.equals("DrReferenceItem") ?
                    Sort.by("sortIndex", "id") : Sort.by("id");
            listMethod.invoke(verify(service), PageRequest.of(0, 20, sort));
            mvc.perform(get(path).param("page", "3").param("size", "5"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.page").value(3))
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.content").isEmpty());
            listMethod.invoke(verify(service), PageRequest.of(3, 5, sort));
        }
    }
}

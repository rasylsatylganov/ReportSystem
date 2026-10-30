package kg.home.demo.service;

import kg.home.demo.entity.DrEnterpriseEntity;
import kg.home.demo.mapper.DrEnterpriseMapper;
import kg.home.demo.model.DrEnterprise;
import kg.home.demo.repository.DrEnterpriseRepository;
import kg.home.demo.service.impl.DrEnterpriseServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DrEnterpriseFilterServiceTest {
    @Test
    void filtersInRepositoryAndPreservesPageTotals() {
        DrEnterpriseRepository repository = mock(DrEnterpriseRepository.class);
        DrEnterpriseMapper mapper = mock(DrEnterpriseMapper.class);
        DrEnterpriseService service = new DrEnterpriseServiceImpl(repository, mapper);
        PageRequest pageable = PageRequest.of(1, 1, Sort.by("id"));
        DrEnterpriseEntity entity = new DrEnterpriseEntity();
        DrEnterprise model = new DrEnterprise(2L, null, "2026-01", "Предприятие 2026",
                2L);
        when(repository.findByTypeEnterprise_Id(7L, pageable))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 3));
        when(mapper.toModel(entity)).thenReturn(model);

        var result = service.findDrEnterprisesByType(7L, pageable);

        assertEquals(List.of(model), result.content());
        assertEquals(3, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(1, result.page());
        verify(repository).findByTypeEnterprise_Id(7L, pageable);
        verifyNoMoreInteractions(repository);
    }
}

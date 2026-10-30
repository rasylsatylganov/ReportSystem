package kg.home.demo.service;

import kg.home.demo.entity.DrPeriodEntity;
import kg.home.demo.mapper.DrPeriodMapper;
import kg.home.demo.model.DrPeriod;
import kg.home.demo.model.PageResult;
import kg.home.demo.repository.DrPeriodRepository;
import kg.home.demo.service.impl.DrPeriodServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaginationServiceTest {

    @Test
    void readsOnlyRequestedPageAndPreservesTotalsWhenMapping() {
        DrPeriodRepository repository = mock(DrPeriodRepository.class);
        DrPeriodMapper mapper = mock(DrPeriodMapper.class);
        DrPeriodEntity entity = new DrPeriodEntity();
        DrPeriod model = new DrPeriod(7L, null, "2026", "2026", null, null);
        PageRequest request = PageRequest.of(2, 20, Sort.by("id"));
        when(repository.findAll(request)).thenReturn(new PageImpl<>(List.of(entity), request, 41));
        when(mapper.toModel(entity)).thenReturn(model);

        PageResult<DrPeriod> result = new DrPeriodServiceImpl(repository, mapper).findAllDrPeriods(request);

        assertEquals(List.of(model), result.content());
        assertEquals(2, result.page());
        assertEquals(20, result.size());
        assertEquals(41, result.totalElements());
        assertEquals(3, result.totalPages());
        verify(repository).findAll(request);
        verify(repository, never()).findAll();
    }
}

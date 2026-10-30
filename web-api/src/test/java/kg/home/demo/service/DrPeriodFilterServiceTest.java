package kg.home.demo.service;

import kg.home.demo.entity.DrPeriodEntity;
import kg.home.demo.mapper.DrPeriodMapper;
import kg.home.demo.model.DrPeriod;
import kg.home.demo.repository.DrPeriodRepository;
import kg.home.demo.service.impl.DrPeriodServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DrPeriodFilterServiceTest {
    @Test
    void filtersInRepositoryAndPreservesPageTotals() {
        DrPeriodRepository repository = mock(DrPeriodRepository.class);
        DrPeriodMapper mapper = mock(DrPeriodMapper.class);
        DrPeriodService service = new DrPeriodServiceImpl(repository, mapper);
        PageRequest pageable = PageRequest.of(1, 1, Sort.by("id"));
        DrPeriodEntity entity = new DrPeriodEntity();
        DrPeriod model = new DrPeriod(2L, null, "2026-01", "Январь 2026",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), 2L);
        when(repository.findByTypePeriod_Id(7L, pageable))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 3));
        when(mapper.toModel(entity)).thenReturn(model);

        var result = service.findDrPeriodsByType(7L, pageable);

        assertEquals(List.of(model), result.content());
        assertEquals(3, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(1, result.page());
        verify(repository).findByTypePeriod_Id(7L, pageable);
        verifyNoMoreInteractions(repository);
    }
}

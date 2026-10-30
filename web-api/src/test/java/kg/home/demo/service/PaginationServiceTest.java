package kg.home.demo.service;

import kg.home.demo.entity.DrPeriodEntity;
import kg.home.demo.entity.DrTypePeriodEntity;
import kg.home.demo.mapper.DrPeriodMapper;
import kg.home.demo.mapper.DrTypePeriodMapper;
import kg.home.demo.model.DrPeriod;
import kg.home.demo.model.DrTypePeriod;
import kg.home.demo.model.PageResult;
import kg.home.demo.repository.DrPeriodRepository;
import kg.home.demo.repository.DrTypePeriodRepository;
import kg.home.demo.service.impl.DrPeriodServiceImpl;
import kg.home.demo.service.impl.DrTypePeriodServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class PaginationServiceTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void typePeriodMovesAcrossPagesUsingGlobalOrder(boolean moveUp) {
        DrTypePeriodRepository repository = mock(DrTypePeriodRepository.class);
        List<DrTypePeriodEntity> entities = List.of(this.typePeriod(1), this.typePeriod(2), this.typePeriod(3));
        when(repository.findAllForUpdate()).thenReturn(entities);
        when(repository.findAll(any(PageRequest.class))).thenAnswer(invocation -> {
            PageRequest request = invocation.getArgument(0);
            List<DrTypePeriodEntity> ordered = entities.stream()
                    .sorted(Comparator.comparing(DrTypePeriodEntity::getSortIndex)
                            .thenComparing(DrTypePeriodEntity::getId)).toList();
            int start = (int) request.getOffset();
            return new PageImpl<>(ordered.subList(start, start + request.getPageSize()), request, ordered.size());
        });
        DrTypePeriodService service = new DrTypePeriodServiceImpl(repository, new DrTypePeriodMapper());
        Sort sort = Sort.by("sortIndex", "id");
        assertEquals(2L, service.findAllDrTypePeriods(PageRequest.of(1, 1, sort)).content().getFirst().id());

        if (moveUp) {
            service.moveUp(2L);
        } else {
            service.moveDown(2L);
        }

        int targetPage = moveUp ? 0 : 2;
        PageResult<DrTypePeriod> result = service.findAllDrTypePeriods(PageRequest.of(targetPage, 1, sort));
        assertEquals(2L, result.content().getFirst().id());
        assertEquals(3, result.totalElements());
        verify(repository).saveAll(List.of(entities.get(1), entities.get(moveUp ? 0 : 2)));
        verify(repository).flush();
    }

    private DrTypePeriodEntity typePeriod(long id) {
        DrTypePeriodEntity entity = new DrTypePeriodEntity();
        entity.setId(id);
        entity.setSortIndex(id);
        return entity;
    }

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

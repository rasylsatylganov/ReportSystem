package kg.home.demo.service;

import kg.home.demo.entity.DrPeriodEntity;
import kg.home.demo.entity.DrTypePeriodEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrPeriodMapper;
import kg.home.demo.model.DrPeriodRequest;
import kg.home.demo.model.IdReference;
import kg.home.demo.repository.DrPeriodRepository;
import kg.home.demo.service.impl.DrPeriodServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DrPeriodOrderTest {
    private final DrPeriodRepository repository = mock(DrPeriodRepository.class);
    private final DrPeriodMapper mapper = mock(DrPeriodMapper.class);
    private final DrPeriodService service = new DrPeriodServiceImpl(repository, mapper);

    @Test
    void createAssignsSequenceOrderAndUpdatePreservesIt() {
        var request = new DrPeriodRequest(new IdReference(1L), "2026", "2026",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
        when(repository.nextSortIndex()).thenReturn(100L);
        when(repository.save(any(DrPeriodEntity.class))).thenAnswer(invocation -> {
            DrPeriodEntity entity = invocation.getArgument(0);
            assertEquals(100L, entity.getSortIndex());
            return entity;
        });
        service.createDrPeriod(request);
        verify(repository).nextSortIndex();

        var existing = period(7, 100, 1);
        when(repository.findAllForUpdate()).thenReturn(List.of(existing));
        service.updateDrPeriod(7L, request);
        assertEquals(100L, existing.getSortIndex());
        verify(repository, times(1)).nextSortIndex();
        verify(repository).save(existing);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void filteredMoveSkipsOtherTypesAndSavesBothRows(boolean up) {
        var first = period(1, 10, 7);
        var otherType = period(2, 20, 8);
        var last = period(3, 30, 7);
        when(repository.findAllForUpdate()).thenReturn(List.of(first, otherType, last));
        if (up) service.moveUp(3L, 7L);
        else service.moveDown(1L, 7L);
        assertEquals(30L, first.getSortIndex());
        assertEquals(20L, otherType.getSortIndex());
        assertEquals(10L, last.getSortIndex());
        verify(repository).saveAll(up ? List.of(last, first) : List.of(first, last));
        verify(repository).flush();
    }

    @Test
    void unfilteredMoveUsesGlobalOrder() {
        var first = period(1, 30, 7);
        var second = period(2, 10, 8);
        var third = period(3, 20, 7);
        when(repository.findAllForUpdate()).thenReturn(List.of(first, second, third));
        service.moveUp(1L, null);
        assertEquals(20L, first.getSortIndex());
        assertEquals(10L, second.getSortIndex());
        assertEquals(30L, third.getSortIndex());
        verify(repository).saveAll(List.of(first, third));
    }

    @Test
    void boundariesAreNoOpsAndWrongTypeOrMissingRowIsNotFound() {
        var existing = period(1, 10, 7);
        when(repository.findAllForUpdate()).thenReturn(List.of(existing));
        service.moveUp(1L, null);
        service.moveDown(1L, 7L);
        assertThrows(ResourceNotFoundException.class, () -> service.moveUp(1L, 8L));
        assertThrows(ResourceNotFoundException.class, () -> service.moveDown(99L, null));
        verify(repository, never()).saveAll(any());
        verify(repository, never()).flush();
    }

    private DrPeriodEntity period(long id, long sortIndex, long typeId) {
        var type = new DrTypePeriodEntity();
        type.setId(typeId);
        var entity = new DrPeriodEntity();
        entity.setId(id);
        entity.setSortIndex(sortIndex);
        entity.setTypePeriod(type);
        return entity;
    }
}

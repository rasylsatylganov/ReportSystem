package kg.home.demo.service;

import kg.home.demo.entity.DrEnterpriseEntity;
import kg.home.demo.entity.DrTypeEnterpriseEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrEnterpriseMapper;
import kg.home.demo.model.DrEnterpriseRequest;
import kg.home.demo.model.IdReference;
import kg.home.demo.repository.DrEnterpriseRepository;
import kg.home.demo.service.impl.DrEnterpriseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DrEnterpriseOrderTest {
    private final DrEnterpriseRepository repository = mock(DrEnterpriseRepository.class);
    private final DrEnterpriseMapper mapper = mock(DrEnterpriseMapper.class);
    private final DrEnterpriseService service = new DrEnterpriseServiceImpl(repository, mapper);

    @Test
    void createAssignsSequenceOrderAndUpdatePreservesIt() {
        var request = new DrEnterpriseRequest(new IdReference(1L), "2026", "2026");

        when(repository.nextSortIndex()).thenReturn(100L);
        when(repository.save(any(DrEnterpriseEntity.class))).thenAnswer(invocation -> {
            DrEnterpriseEntity entity = invocation.getArgument(0);
            assertEquals(100L, entity.getSortIndex());
            return entity;
        });
        service.createDrEnterprise(request);
        verify(repository).nextSortIndex();

        var existing = enterprise(7, 100, 1);
        when(repository.findAllForUpdate()).thenReturn(List.of(existing));
        service.updateDrEnterprise(7L, request);
        assertEquals(100L, existing.getSortIndex());
        verify(repository, times(1)).nextSortIndex();
        verify(repository).save(existing);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void filteredMoveSkipsOtherTypesAndSavesBothRows(boolean up) {
        var first = enterprise(1, 10, 7);
        var otherType = enterprise(2, 20, 8);
        var last = enterprise(3, 30, 7);
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
        var first = enterprise(1, 30, 7);
        var second = enterprise(2, 10, 8);
        var third = enterprise(3, 20, 7);
        when(repository.findAllForUpdate()).thenReturn(List.of(first, second, third));
        service.moveUp(1L, null);
        assertEquals(20L, first.getSortIndex());
        assertEquals(10L, second.getSortIndex());
        assertEquals(30L, third.getSortIndex());
        verify(repository).saveAll(List.of(first, third));
    }

    @Test
    void boundariesAreNoOpsAndWrongTypeOrMissingRowIsNotFound() {
        var existing = enterprise(1, 10, 7);
        when(repository.findAllForUpdate()).thenReturn(List.of(existing));
        service.moveUp(1L, null);
        service.moveDown(1L, 7L);
        assertThrows(ResourceNotFoundException.class, () -> service.moveUp(1L, 8L));
        assertThrows(ResourceNotFoundException.class, () -> service.moveDown(99L, null));
        verify(repository, never()).saveAll(any());
        verify(repository, never()).flush();
    }

    private DrEnterpriseEntity enterprise(long id, long sortIndex, long typeId) {
        var type = new DrTypeEnterpriseEntity();
        type.setId(typeId);
        var entity = new DrEnterpriseEntity();
        entity.setId(id);
        entity.setSortIndex(sortIndex);
        entity.setTypeEnterprise(type);
        return entity;
    }
}

package kg.home.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.home.demo.entity.DrReferenceEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrReferenceMapper;
import kg.home.demo.model.DrReference;
import kg.home.demo.model.DrReferenceRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.repository.DrReferenceRepository;
import kg.home.demo.service.impl.DrReferenceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DrReferenceServiceTest {

    private final DrReferenceRepository repository = mock(DrReferenceRepository.class);
    private final DrReferenceService service = new DrReferenceServiceImpl(
            this.repository, new DrReferenceMapper());

    @Test
    void createAssignsServerOrderAndIgnoresClientIdAndSortIndex() throws Exception {
        DrReferenceRequest request = new ObjectMapper().readValue("""
                {"id":999,"sortIndex":-1,"codeReference":"BANK","nameReference":"Банк"}
                """, DrReferenceRequest.class);
        when(this.repository.nextSortIndex()).thenReturn(50L);
        when(this.repository.save(any(DrReferenceEntity.class))).thenAnswer(invocation -> {
            DrReferenceEntity entity = invocation.getArgument(0);
            assertNull(entity.getId());
            assertEquals(50L, entity.getSortIndex());
            entity.setId(7L);
            return entity;
        });

        assertEquals(new DrReference(7L, "BANK", "Банк", 50L), this.service.createDrReference(request));
        verify(this.repository).findAllForUpdate();
    }

    @Test
    void updatePreservesExistingOrder() {
        DrReferenceEntity entity = this.entity(7, 50);
        when(this.repository.findAllForUpdate()).thenReturn(List.of(entity));
        when(this.repository.save(entity)).thenReturn(entity);

        DrReference result = this.service.updateDrReference(7L,
                new DrReferenceRequest("BANK", "Банк"));

        assertEquals(new DrReference(7L, "BANK", "Банк", 50L), result);
        verify(this.repository, never()).nextSortIndex();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void movesAcrossPagesInSortIndexOrder(boolean up) {
        List<DrReferenceEntity> entities = List.of(this.entity(1, 30), this.entity(2, 10), this.entity(3, 20));
        when(this.repository.findAllForUpdate()).thenReturn(entities);
        when(this.repository.findAll(any(PageRequest.class))).thenAnswer(invocation -> {
            PageRequest page = invocation.getArgument(0);
            List<DrReferenceEntity> ordered = entities.stream()
                    .sorted(Comparator.comparing(DrReferenceEntity::getSortIndex)
                            .thenComparing(DrReferenceEntity::getId)).toList();
            int offset = (int) page.getOffset();
            return new PageImpl<>(ordered.subList(offset, offset + page.getPageSize()), page, ordered.size());
        });

        if (up) {
            this.service.moveUp(3L);
        } else {
            this.service.moveDown(3L);
        }

        PageResult<DrReference> result = this.service.findAllDrReferences(
                PageRequest.of(up ? 0 : 2, 1, Sort.by("sortIndex", "id")));
        assertEquals(3L, result.content().getFirst().id());
        assertEquals(3, result.totalElements());
        verify(this.repository).saveAll(List.of(entities.get(2), entities.get(up ? 1 : 0)));
        verify(this.repository).flush();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void boundaryMoveDoesNotWrite(boolean up) {
        when(this.repository.findAllForUpdate()).thenReturn(List.of(this.entity(7, 50)));

        if (up) {
            this.service.moveUp(7L);
        } else {
            this.service.moveDown(7L);
        }

        verify(this.repository, never()).saveAll(any());
        verify(this.repository, never()).flush();
    }

    @Test
    void missingRecordRaisesDomainError() {
        when(this.repository.findAllForUpdate()).thenReturn(List.of());
        assertThrows(ResourceNotFoundException.class, () -> this.service.moveUp(99L));
        assertThrows(ResourceNotFoundException.class, () -> this.service.updateDrReference(99L,
                new DrReferenceRequest("BANK", "Банк")));
        assertThrows(ResourceNotFoundException.class, () -> this.service.deleteDrReference(99L));
    }

    @Test
    void deleteUsesLockedRecord() {
        DrReferenceEntity entity = this.entity(7, 50);
        when(this.repository.findAllForUpdate()).thenReturn(List.of(entity));
        this.service.deleteDrReference(7L);
        verify(this.repository).delete(entity);
    }

    private DrReferenceEntity entity(long id, long sortIndex) {
        DrReferenceEntity entity = new DrReferenceEntity();
        entity.setId(id);
        entity.setSortIndex(sortIndex);
        return entity;
    }
}

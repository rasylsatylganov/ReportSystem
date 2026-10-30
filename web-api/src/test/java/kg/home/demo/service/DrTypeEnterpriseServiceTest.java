package kg.home.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.home.demo.entity.DrTypeEnterpriseEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrTypeEnterpriseMapper;
import kg.home.demo.model.DrTypeEnterprise;
import kg.home.demo.model.DrTypeEnterpriseRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.repository.DrTypeEnterpriseRepository;
import kg.home.demo.service.impl.DrTypeEnterpriseServiceImpl;
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

class DrTypeEnterpriseServiceTest {

    private final DrTypeEnterpriseRepository repository = mock(DrTypeEnterpriseRepository.class);
    private final DrTypeEnterpriseService service = new DrTypeEnterpriseServiceImpl(
            this.repository, new DrTypeEnterpriseMapper());

    @Test
    void createAssignsServerOrderAndIgnoresClientIdAndSortIndex() throws Exception {
        DrTypeEnterpriseRequest request = new ObjectMapper().readValue("""
                {"id":999,"sortIndex":-1,"codeTypeEnterprise":"BANK","nameTypeEnterprise":"Банк"}
                """, DrTypeEnterpriseRequest.class);
        when(this.repository.nextSortIndex()).thenReturn(50L);
        when(this.repository.save(any(DrTypeEnterpriseEntity.class))).thenAnswer(invocation -> {
            DrTypeEnterpriseEntity entity = invocation.getArgument(0);
            assertNull(entity.getId());
            assertEquals(50L, entity.getSortIndex());
            entity.setId(7L);
            return entity;
        });

        assertEquals(new DrTypeEnterprise(7L, "BANK", "Банк", 50L), this.service.createDrTypeEnterprise(request));
        verify(this.repository).findAllForUpdate();
    }

    @Test
    void updatePreservesExistingOrder() {
        DrTypeEnterpriseEntity entity = this.entity(7, 50);
        when(this.repository.findAllForUpdate()).thenReturn(List.of(entity));
        when(this.repository.save(entity)).thenReturn(entity);

        DrTypeEnterprise result = this.service.updateDrTypeEnterprise(7L,
                new DrTypeEnterpriseRequest("BANK", "Банк"));

        assertEquals(new DrTypeEnterprise(7L, "BANK", "Банк", 50L), result);
        verify(this.repository, never()).nextSortIndex();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void movesAcrossPagesInSortIndexOrder(boolean up) {
        List<DrTypeEnterpriseEntity> entities = List.of(this.entity(1, 30), this.entity(2, 10), this.entity(3, 20));
        when(this.repository.findAllForUpdate()).thenReturn(entities);
        when(this.repository.findAll(any(PageRequest.class))).thenAnswer(invocation -> {
            PageRequest page = invocation.getArgument(0);
            List<DrTypeEnterpriseEntity> ordered = entities.stream()
                    .sorted(Comparator.comparing(DrTypeEnterpriseEntity::getSortIndex)
                            .thenComparing(DrTypeEnterpriseEntity::getId)).toList();
            int offset = (int) page.getOffset();
            return new PageImpl<>(ordered.subList(offset, offset + page.getPageSize()), page, ordered.size());
        });

        if (up) {
            this.service.moveUp(3L);
        } else {
            this.service.moveDown(3L);
        }

        PageResult<DrTypeEnterprise> result = this.service.findAllDrTypeEnterprises(
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
        assertThrows(ResourceNotFoundException.class, () -> this.service.updateDrTypeEnterprise(99L,
                new DrTypeEnterpriseRequest("BANK", "Банк")));
        assertThrows(ResourceNotFoundException.class, () -> this.service.deleteDrTypeEnterprise(99L));
    }

    @Test
    void deleteUsesLockedRecord() {
        DrTypeEnterpriseEntity entity = this.entity(7, 50);
        when(this.repository.findAllForUpdate()).thenReturn(List.of(entity));
        this.service.deleteDrTypeEnterprise(7L);
        verify(this.repository).delete(entity);
    }

    private DrTypeEnterpriseEntity entity(long id, long sortIndex) {
        DrTypeEnterpriseEntity entity = new DrTypeEnterpriseEntity();
        entity.setId(id);
        entity.setSortIndex(sortIndex);
        return entity;
    }
}

package kg.home.demo.service;

import kg.home.demo.entity.DrReferenceItemEntity;
import kg.home.demo.mapper.DrReferenceItemMapper;
import kg.home.demo.model.DrReferenceItem;
import kg.home.demo.repository.DrReferenceItemRepository;
import kg.home.demo.service.impl.DrReferenceItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DrReferenceItemFilterServiceTest {
    @Test
    void filtersInRepositoryAndPreservesPageTotals() {
        DrReferenceItemRepository repository = mock(DrReferenceItemRepository.class);
        DrReferenceItemMapper mapper = mock(DrReferenceItemMapper.class);
        DrReferenceItemService service = new DrReferenceItemServiceImpl(repository, mapper);
        PageRequest pageable = PageRequest.of(1, 1, Sort.by("id"));
        DrReferenceItemEntity entity = new DrReferenceItemEntity();
        DrReferenceItem model = new DrReferenceItem(2L, null, "2026-01", "Значение справочника 2026",
                2L);
        when(repository.findByReference_Id(7L, pageable))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 3));
        when(mapper.toModel(entity)).thenReturn(model);

        var result = service.findDrReferenceItemsByReference(7L, pageable);

        assertEquals(List.of(model), result.content());
        assertEquals(3, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(1, result.page());
        verify(repository).findByReference_Id(7L, pageable);
        verifyNoMoreInteractions(repository);
    }
}

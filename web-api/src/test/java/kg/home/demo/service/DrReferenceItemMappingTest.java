package kg.home.demo.service;

import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.persistence.EntityManager;
import kg.home.demo.entity.DrReferenceEntity;
import kg.home.demo.entity.DrReferenceItemEntity;
import kg.home.demo.mapper.DrReferenceItemMapper;
import kg.home.demo.mapper.DrReferenceMapper;
import kg.home.demo.mapper.JpaReferenceResolver;
import kg.home.demo.model.DrReferenceItemRequest;
import kg.home.demo.repository.DrReferenceItemRepository;
import kg.home.demo.service.impl.DrReferenceItemServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DrReferenceItemMappingTest {
    @Test
    void usesManagedReferenceAndServerSortIndexWithoutLegacyOrderSort() throws Exception {
        var json = JsonMapper.builder().findAndAddModules().build();
        var request = json.readValue("""
                {"reference":{"id":7},"codeReferenceItem":"KG","nameReferenceItem":"Кыргызстан",
                 "id":999,"sortIndex":-1,"orderSort":-1}
                """, DrReferenceItemRequest.class);
        var entityManager = mock(EntityManager.class);
        var reference = new DrReferenceEntity();
        reference.setId(7L);
        reference.setNameReference("Страны");
        when(entityManager.getReference(DrReferenceEntity.class, 7L)).thenReturn(reference);
        var repository = mock(DrReferenceItemRepository.class);
        when(repository.nextSortIndex()).thenReturn(100L);
        when(repository.save(any(DrReferenceItemEntity.class))).thenAnswer(invocation -> {
            DrReferenceItemEntity entity = invocation.getArgument(0);
            assertNull(entity.getId());
            assertSame(reference, entity.getReference());
            entity.setId(10L);
            return entity;
        });
        var mapper = new DrReferenceItemMapper(new JpaReferenceResolver(entityManager), new DrReferenceMapper());
        var service = new DrReferenceItemServiceImpl(repository, mapper);
        var result = service.createDrReferenceItem(request);
        assertEquals(100L, result.sortIndex());
        assertEquals("Страны", result.reference().nameReference());
        var output = json.valueToTree(result);
        assertTrue(output.has("sortIndex"));
        assertFalse(output.has("orderSort"));
        assertFalse(output.toString().contains("hibernateLazyInitializer"));
    }
}

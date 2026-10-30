package kg.home.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.persistence.EntityManager;
import kg.home.demo.entity.DrPeriodEntity;
import kg.home.demo.entity.DrTypePeriodEntity;
import kg.home.demo.mapper.DrPeriodMapper;
import kg.home.demo.mapper.DrTypePeriodMapper;
import kg.home.demo.mapper.JpaReferenceResolver;
import kg.home.demo.model.DrPeriod;
import kg.home.demo.model.DrPeriodRequest;
import kg.home.demo.model.DrTypePeriod;
import kg.home.demo.model.DrTypePeriodRequest;
import kg.home.demo.model.MdReportInReferenceItemRequest;
import kg.home.demo.repository.DrPeriodRepository;
import kg.home.demo.repository.DrTypePeriodRepository;
import kg.home.demo.service.impl.DrPeriodServiceImpl;
import kg.home.demo.service.impl.DrTypePeriodServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceModelMappingTest {


    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void createAssignsServerFieldsAndKeepsJsonContract() throws Exception {
        DrTypePeriodRepository repository = mock(DrTypePeriodRepository.class);
        DrTypePeriodService service = new DrTypePeriodServiceImpl(repository, new DrTypePeriodMapper());
        DrTypePeriodRequest request = this.objectMapper.readValue("""
                {"id":999,"sortIndex":-10,"codeTypePeriod":"MON","nameTypePeriod":"Месячный"}
                """, DrTypePeriodRequest.class);
        when(repository.nextSortIndex()).thenReturn(50L);
        when(repository.save(any(DrTypePeriodEntity.class))).thenAnswer(invocation -> {
            DrTypePeriodEntity entity = invocation.getArgument(0);
            assertNull(entity.getId());
            entity.setId(7L);
            return entity;
        });

        DrTypePeriod result = service.createDrTypePeriod(request);

        assertEquals(new DrTypePeriod(7L, "MON", "Месячный", 50L), result);
        JsonNode json = this.objectMapper.valueToTree(result);
        assertEquals(4, json.size());
        for (String field : Set.of("id", "sortIndex", "codeTypePeriod", "nameTypePeriod")) {
            assertTrue(json.has(field), field);
        }
        verify(repository).findAllForUpdate();
    }

    @Test
    void updatePreservesPathIdAndSortIndex() throws Exception {
        DrTypePeriodRepository repository = mock(DrTypePeriodRepository.class);
        DrTypePeriodEntity entity = this.typePeriodEntity(7L, 50L);
        when(repository.findAllForUpdate()).thenReturn(List.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        DrTypePeriodRequest request = this.objectMapper.readValue("""
                {"id":999,"sortIndex":-10,"codeTypePeriod":"QUA","nameTypePeriod":"Квартальный"}
                """, DrTypePeriodRequest.class);

        DrTypePeriodService service = new DrTypePeriodServiceImpl(repository, new DrTypePeriodMapper());
        DrTypePeriod result = service.updateDrTypePeriod(7L, request);

        assertEquals(new DrTypePeriod(7L, "QUA", "Квартальный", 50L), result);
    }

    @Test
    void listReturnsIndependentSnapshotsInRepositoryOrder() {
        DrTypePeriodRepository repository = mock(DrTypePeriodRepository.class);
        DrTypePeriodEntity first = this.typePeriodEntity(20L, 1L);
        DrTypePeriodEntity second = this.typePeriodEntity(10L, 2L);
        PageRequest page = PageRequest.of(0, 20, Sort.by("sortIndex", "id"));
        when(repository.findAll(page)).thenReturn(new PageImpl<>(List.of(first, second), page, 2));

        DrTypePeriodService service = new DrTypePeriodServiceImpl(repository, new DrTypePeriodMapper());
        List<DrTypePeriod> result = service.findAllDrTypePeriods(page).content();
        first.setNameTypePeriod("Изменено после чтения");

        assertEquals(List.of(20L, 10L), result.stream().map(DrTypePeriod::id).toList());
        assertEquals("Месячный", result.getFirst().nameTypePeriod());
    }

    @Test
    void createPeriodUsesManagedReferenceAndReturnsNestedModel() throws Exception {
        DrPeriodRepository repository = mock(DrPeriodRepository.class);
        when(repository.nextSortIndex()).thenReturn(60L);
        EntityManager entityManager = mock(EntityManager.class);
        DrTypePeriodEntity typePeriodEntity = this.typePeriodEntity(7L, 50L);
        DrTypePeriod typePeriod = new DrTypePeriod(7L, "MON", "Месячный", 50L);
        when(entityManager.getReference(DrTypePeriodEntity.class, 7L)).thenReturn(typePeriodEntity);
        when(repository.save(any(DrPeriodEntity.class))).thenAnswer(invocation -> {
            DrPeriodEntity entity = invocation.getArgument(0);
            assertNull(entity.getId());
            assertSame(typePeriodEntity, entity.getTypePeriod());
            entity.setId(12L);
            return entity;
        });
        DrPeriodRequest request = this.objectMapper.readValue("""
                {"id":999,"sortIndex":-1,"typePeriod":{"id":7,"nameTypePeriod":"Подмена"},
                 "codePeriod":"2026M01","namePeriod":"Январь 2026",
                 "dateBegin":"2026-01-01","dateEnd":"2026-01-31"}
                """, DrPeriodRequest.class);
        DrPeriodMapper mapper = new DrPeriodMapper(
                new JpaReferenceResolver(entityManager), new DrTypePeriodMapper());
        DrPeriodService service = new DrPeriodServiceImpl(repository, mapper);

        DrPeriod result = service.createDrPeriod(request);

        assertEquals(12L, result.id());
        assertEquals(60L, result.sortIndex());
        assertEquals(typePeriod, result.typePeriod());
        assertEquals(LocalDate.of(2026, 1, 1), result.dateBegin());
        JsonNode json = this.objectMapper.valueToTree(result);
        assertEquals("Месячный", json.path("typePeriod").path("nameTypePeriod").asText());
        assertFalse(json.toString().contains("hibernateLazyInitializer"));
    }

    @Test
    void missingRecordReturnsEmptyOptional() {
        DrTypePeriodRepository repository = mock(DrTypePeriodRepository.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        DrTypePeriodService service = new DrTypePeriodServiceImpl(repository, new DrTypePeriodMapper());
        assertTrue(service.findDrTypePeriodById(99L).isEmpty());
    }

    @Test
    void omittedRequiredFlagKeepsDefaultValue() throws Exception {
        MdReportInReferenceItemRequest request = this.objectMapper.readValue("""
                {"reportIn":{"id":1},"referenceItem":{"id":2},
                 "typeFieldValue":{"id":3},"indexColumn":4,"orderSort":5}
                """, MdReportInReferenceItemRequest.class);

        assertEquals(1, request.isRequired());
    }

    @Test
    void mappersCanBeWiredWithoutServicesOrRepositories() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(EntityManager.class, () -> mock(EntityManager.class));
            context.scan("kg.home.demo.mapper");
            context.refresh();
            for (Class<?> serviceType : ModelBoundaryTest.serviceTypes()) {
                String mapperName = serviceType.getSimpleName().replace("Service", "Mapper");
                Class<?> mapperType = Class.forName("kg.home.demo.mapper." + mapperName);
                assertTrue(mapperType.isInstance(context.getBean(mapperType)));
            }
        }
    }

    private DrTypePeriodEntity typePeriodEntity(Long id, Long sortIndex) {
        DrTypePeriodEntity entity = new DrTypePeriodEntity();
        entity.setId(id);
        entity.setSortIndex(sortIndex);
        entity.setCodeTypePeriod("MON");
        entity.setNameTypePeriod("Месячный");
        return entity;
    }
}

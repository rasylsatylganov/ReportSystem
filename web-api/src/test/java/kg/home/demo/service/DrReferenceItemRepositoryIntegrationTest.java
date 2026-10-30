package kg.home.demo.service;

import kg.home.demo.repository.DrReferenceItemRepository;
import kg.home.demo.repository.DrReferenceRepository;
import kg.home.demo.model.DrReferenceItemRequest;
import kg.home.demo.model.IdReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@Transactional(readOnly = true)
class DrReferenceItemRepositoryIntegrationTest {
    @Autowired
    private DrReferenceItemRepository repository;

    @Autowired
    private DrReferenceItemService service;

    @Autowired
    private DrReferenceRepository types;

    @Test
    @Transactional
    void createsUpdatesAndDeletesWithServerOrderAndRollsBack() {
        var availableTypes = types.findAll(PageRequest.of(0, 1));
        assumeFalse(availableTypes.isEmpty(), "A type is required to create an item");
        var type = availableTypes.getContent().getFirst();
        String code = "TEST_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        var created = service.createDrReferenceItem(new DrReferenceItemRequest(
                new IdReference(type.getId()), code, "Тестовое значение"));
        assertNotNull(created.sortIndex());
        assertEquals(type.getId(), created.reference().id());
        var updated = service.updateDrReferenceItem(created.id(), new DrReferenceItemRequest(
                new IdReference(type.getId()), code, "Обновленное значение"));
        assertEquals(created.sortIndex(), updated.sortIndex());
        assertEquals("Обновленное значение", updated.nameReferenceItem());
        service.deleteDrReferenceItem(created.id());
        assertTrue(service.findDrReferenceItemById(created.id()).isEmpty());
    }

    @Test
    @Transactional
    void movesAcrossPagesOnOracleAndRollsBackChanges() {
        var pageable = PageRequest.of(0, 2, Sort.by("sortIndex", "id"));
        var all = repository.findAll(pageable);
        if (all.getNumberOfElements() < 2) return;
        Long movingId = all.getContent().get(1).getId();
        service.moveUp(movingId, null);
        assertEquals(movingId, service.findAllDrReferenceItems(PageRequest.of(0, 1, pageable.getSort()))
                .content().getFirst().id());
        service.moveDown(movingId, null);

        Long typeId = all.getContent().getFirst().getReference().getId();
        var filtered = repository.findByReference_Id(typeId, pageable);
        if (filtered.getNumberOfElements() < 2) return;
        Long filteredId = filtered.getContent().get(1).getId();
        service.moveUp(filteredId, typeId);
        assertEquals(filteredId, service.findDrReferenceItemsByReference(typeId,
                PageRequest.of(0, 1, pageable.getSort())).content().getFirst().id());
    }

    @Test
    void readsPagesWithTypeOnOracle() {
        var pageable = PageRequest.of(0, 1, Sort.by("sortIndex", "id"));
        var all = repository.findAll(pageable);
        all.forEach(item -> assertNotNull(item.getReference().getNameReference()));
        long typeId = all.isEmpty() ? 1L : all.getContent().getFirst().getReference().getId();
        var filtered = repository.findByReference_Id(typeId, pageable);
        filtered.forEach(item -> assertNotNull(item.getReference().getNameReference()));
    }
}

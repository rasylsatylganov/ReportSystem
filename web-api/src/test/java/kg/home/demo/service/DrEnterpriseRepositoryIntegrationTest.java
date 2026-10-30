package kg.home.demo.service;

import kg.home.demo.repository.DrEnterpriseRepository;
import kg.home.demo.repository.DrTypeEnterpriseRepository;
import kg.home.demo.model.DrEnterpriseRequest;
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
class DrEnterpriseRepositoryIntegrationTest {
    @Autowired
    private DrEnterpriseRepository repository;

    @Autowired
    private DrEnterpriseService service;

    @Autowired
    private DrTypeEnterpriseRepository types;

    @Test
    @Transactional
    void createsUpdatesAndDeletesWithServerOrderAndRollsBack() {
        var availableTypes = types.findAll(PageRequest.of(0, 1));
        assumeFalse(availableTypes.isEmpty(), "A type is required to create an enterprise");
        var type = availableTypes.getContent().getFirst();
        String code = "TEST_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        var created = service.createDrEnterprise(new DrEnterpriseRequest(
                new IdReference(type.getId()), code, "Тестовое предприятие"));
        assertNotNull(created.sortIndex());
        assertEquals(type.getId(), created.typeEnterprise().id());
        var updated = service.updateDrEnterprise(created.id(), new DrEnterpriseRequest(
                new IdReference(type.getId()), code, "Обновленное предприятие"));
        assertEquals(created.sortIndex(), updated.sortIndex());
        assertEquals("Обновленное предприятие", updated.nameEnterprise());
        service.deleteDrEnterprise(created.id());
        assertTrue(service.findDrEnterpriseById(created.id()).isEmpty());
    }

    @Test
    @Transactional
    void movesAcrossPagesOnOracleAndRollsBackChanges() {
        var pageable = PageRequest.of(0, 2, Sort.by("sortIndex", "id"));
        var all = repository.findAll(pageable);
        if (all.getNumberOfElements() < 2) return;
        Long movingId = all.getContent().get(1).getId();
        service.moveUp(movingId, null);
        assertEquals(movingId, service.findAllDrEnterprises(PageRequest.of(0, 1, pageable.getSort()))
                .content().getFirst().id());
        service.moveDown(movingId, null);

        Long typeId = all.getContent().getFirst().getTypeEnterprise().getId();
        var filtered = repository.findByTypeEnterprise_Id(typeId, pageable);
        if (filtered.getNumberOfElements() < 2) return;
        Long filteredId = filtered.getContent().get(1).getId();
        service.moveUp(filteredId, typeId);
        assertEquals(filteredId, service.findDrEnterprisesByType(typeId,
                PageRequest.of(0, 1, pageable.getSort())).content().getFirst().id());
    }

    @Test
    void readsPagesWithTypeOnOracle() {
        var pageable = PageRequest.of(0, 1, Sort.by("sortIndex", "id"));
        var all = repository.findAll(pageable);
        all.forEach(enterprise -> assertNotNull(enterprise.getTypeEnterprise().getNameTypeEnterprise()));
        long typeId = all.isEmpty() ? 1L : all.getContent().getFirst().getTypeEnterprise().getId();
        var filtered = repository.findByTypeEnterprise_Id(typeId, pageable);
        filtered.forEach(enterprise -> assertNotNull(enterprise.getTypeEnterprise().getNameTypeEnterprise()));
    }
}

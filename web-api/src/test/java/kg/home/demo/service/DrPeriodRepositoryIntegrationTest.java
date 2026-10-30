package kg.home.demo.service;

import kg.home.demo.repository.DrPeriodRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@Transactional(readOnly = true)
class DrPeriodRepositoryIntegrationTest {
    @Autowired
    private DrPeriodRepository repository;

    @Autowired
    private DrPeriodService service;

    @Test
    @Transactional
    void movesAcrossPagesOnOracleAndRollsBackChanges() {
        var pageable = PageRequest.of(0, 2, Sort.by("sortIndex", "id"));
        var all = repository.findAll(pageable);
        if (all.getNumberOfElements() < 2) return;
        Long movingId = all.getContent().get(1).getId();
        service.moveUp(movingId, null);
        assertEquals(movingId, service.findAllDrPeriods(PageRequest.of(0, 1, pageable.getSort()))
                .content().getFirst().id());
        service.moveDown(movingId, null);

        Long typeId = all.getContent().getFirst().getTypePeriod().getId();
        var filtered = repository.findByTypePeriod_Id(typeId, pageable);
        if (filtered.getNumberOfElements() < 2) return;
        Long filteredId = filtered.getContent().get(1).getId();
        service.moveUp(filteredId, typeId);
        assertEquals(filteredId, service.findDrPeriodsByType(typeId,
                PageRequest.of(0, 1, pageable.getSort())).content().getFirst().id());
    }

    @Test
    void readsPagesWithTypeOnOracle() {
        var pageable = PageRequest.of(0, 1, Sort.by("sortIndex", "id"));
        var all = repository.findAll(pageable);
        all.forEach(period -> assertNotNull(period.getTypePeriod().getNameTypePeriod()));
        long typeId = all.isEmpty() ? 1L : all.getContent().getFirst().getTypePeriod().getId();
        var filtered = repository.findByTypePeriod_Id(typeId, pageable);
        filtered.forEach(period -> assertNotNull(period.getTypePeriod().getNameTypePeriod()));
    }
}

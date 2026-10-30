package kg.home.demo.repository;

import kg.home.demo.entity.DrPeriodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DrPeriodRepository extends JpaRepository<DrPeriodEntity, Long> {
    // Oracle 12.1 rejects duplicate column names in joined paginated queries.
    // Keep the page query on one table; types are loaded in a separate batch.
    Page<DrPeriodEntity> findByTypePeriod_Id(Long typePeriodId, Pageable pageable);

    @Query(value = "SELECT DR_PERIOD_SORT_SEQ.NEXTVAL FROM dual", nativeQuery = true)
    Long nextSortIndex();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from DrPeriodEntity p order by p.id")
    List<DrPeriodEntity> findAllForUpdate();
}

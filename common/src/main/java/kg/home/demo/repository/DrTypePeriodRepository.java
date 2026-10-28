package kg.home.demo.repository;

import jakarta.persistence.LockModeType;
import kg.home.demo.entity.DrTypePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DrTypePeriodRepository extends JpaRepository<DrTypePeriod, Long> {

    List<DrTypePeriod> findAllByOrderBySortIndexAscIdAsc();

    @Query(value = "SELECT DR_TYPE_PERIOD_SORT_SEQ.NEXTVAL FROM dual", nativeQuery = true)
    Long nextSortIndex();

    // Справочник маленький: блокируем его записи в одном порядке по ID.
    // Вызывать только внутри транзакции записи.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from DrTypePeriod p order by p.id")
    List<DrTypePeriod> findAllForUpdate();
}

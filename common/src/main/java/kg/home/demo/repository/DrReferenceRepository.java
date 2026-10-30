package kg.home.demo.repository;

import jakarta.persistence.LockModeType;
import kg.home.demo.entity.DrReferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DrReferenceRepository extends JpaRepository<DrReferenceEntity, Long> {

    @Query(value = "SELECT DR_REFERENCE_SORT_SEQ.NEXTVAL FROM dual", nativeQuery = true)
    Long nextSortIndex();

    // Справочник маленький: блокируем его записи в одном порядке по ID.
    // Вызывать только внутри транзакции записи.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from DrReferenceEntity p order by p.id")
    List<DrReferenceEntity> findAllForUpdate();
}

package kg.home.demo.repository;

import kg.home.demo.entity.DrReferenceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DrReferenceItemRepository extends JpaRepository<DrReferenceItemEntity, Long> {
    // Oracle 12.1 rejects duplicate column names in joined paginated queries.
    // Keep the page query on one table; types are loaded in a separate batch.
    Page<DrReferenceItemEntity> findByReference_Id(Long referenceId, Pageable pageable);

    @Query(value = "SELECT DR_REFERENCE_ITEM_SORT_SEQ.NEXTVAL FROM dual", nativeQuery = true)
    Long nextSortIndex();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from DrReferenceItemEntity p order by p.id")
    List<DrReferenceItemEntity> findAllForUpdate();
}

package kg.home.demo.repository;

import kg.home.demo.entity.MdReportInEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface MdReportInRepository extends JpaRepository<MdReportInEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from MdReportInEntity r where r.id = :id")
    Optional<MdReportInEntity> findForUpdate(@Param("id") Long id);
}

package kg.home.demo.repository;

import kg.home.demo.entity.MdReportInEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MdReportInRepository extends JpaRepository<MdReportInEntity, Long> {
    @Query("select r from MdReportInEntity r where r.typePeriod.id = ?1 and r.typeEnterprise.id = ?2 " +
            "and upper(r.codeReportIn) = upper(?3)")
    Optional<MdReportInEntity> findByTypePeriod_IdAndTypeEnterprise_IdAndCodeReportInIgnoreCase(
            Long typePeriodId, Long typeEnterpriseId, String code);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from MdReportInEntity r where r.id = :id")
    Optional<MdReportInEntity> findForUpdate(@Param("id") Long id);

    @Query("select r from MdReportInEntity r " +
            "where (:typePeriodId is null or r.typePeriod.id = :typePeriodId) " +
            "and (:typeEnterpriseId is null or r.typeEnterprise.id = :typeEnterpriseId)")
    Page<MdReportInEntity> findByTypes(@Param("typePeriodId") Long typePeriodId,
            @Param("typeEnterpriseId") Long typeEnterpriseId, Pageable pageable);
}

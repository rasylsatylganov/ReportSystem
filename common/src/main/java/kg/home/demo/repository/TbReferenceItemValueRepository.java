package kg.home.demo.repository;

import kg.home.demo.entity.TbReferenceItemValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TbReferenceItemValueRepository extends JpaRepository<TbReferenceItemValueEntity, Long> {
    String VIEW_ROWS = """
            select v.INDEX_ROW as DATA_ROW
            from TB_REFERENCE_ITEM_VALUE v
            join TB_REFERENCE_ITEM r on r.ID = v.REFERENCE_ITEM_ID
            join MD_REPORT_IN_REFERENCE mr on mr.ID = r.REPORT_IN_REFERENCE_ID
            join MD_REPORT_IN_REFERENCE_ITEM f on f.ID = v.REPORT_IN_REFERENCE_ITEM_ID
            where r.PERIOD_ID = :periodId and r.ENTERPRISE_ID = :enterpriseId
              and mr.REPORT_IN_ID = :reportId and f.REPORT_IN_ID = :reportId
            union
            select r.INDEX_ROW as DATA_ROW from TB_REFERENCE_ITEM r
            where r.PERIOD_ID = :periodId and r.ENTERPRISE_ID = :enterpriseId
              and r.REPORT_IN_REFERENCE_ID in (:verticalReferenceIds)
            """;

    boolean existsByReportInReferenceItem_Id(Long metadataId);

    // One projected column avoids Oracle 12.1's duplicate-column pagination restriction.
    @Query(value = "select DATA_ROW from (" + VIEW_ROWS + ") order by DATA_ROW",
            countQuery = "select count(*) from (" + VIEW_ROWS + ")", nativeQuery = true)
    Page<Number> findViewRows(@Param("reportId") Long reportId, @Param("periodId") Long periodId,
            @Param("enterpriseId") Long enterpriseId, @Param("verticalReferenceIds") List<Long> verticalReferenceIds,
            Pageable pageable);

    @Query("""
            select v from TbReferenceItemValueEntity v
            where v.referenceItem.period.id = :periodId and v.referenceItem.enterprise.id = :enterpriseId
              and v.referenceItem.reportInReference.reportIn.id = :reportId
              and v.reportInReferenceItem.reportIn.id = :reportId and v.indexRow in :rows
            order by v.indexRow, v.id
            """)
    List<TbReferenceItemValueEntity> findViewValues(@Param("reportId") Long reportId,
            @Param("periodId") Long periodId, @Param("enterpriseId") Long enterpriseId,
            @Param("rows") List<Long> rows);
}

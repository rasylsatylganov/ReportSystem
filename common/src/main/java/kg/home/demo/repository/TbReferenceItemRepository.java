package kg.home.demo.repository;

import kg.home.demo.entity.TbReferenceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TbReferenceItemRepository extends JpaRepository<TbReferenceItemEntity, Long> {
    boolean existsByPeriod_IdAndEnterprise_IdAndReportInReference_ReportIn_Id(
            Long periodId, Long enterpriseId, Long reportId);
    boolean existsByReportInReference_Id(Long metadataId);

    // Include the last marker of each dimension before the page to continue groups across pages.
    @Query("""
            select r from TbReferenceItemEntity r join fetch r.referenceItem
            where r.period.id = :periodId and r.enterprise.id = :enterpriseId
              and r.reportInReference.id in :referenceIds
              and (r.indexRow between :firstRow and :lastRow or
                   (r.indexRow < :firstRow and not exists (
                       select newer.id from TbReferenceItemEntity newer
                       where newer.period.id = r.period.id and newer.enterprise.id = r.enterprise.id
                         and newer.reportInReference.id = r.reportInReference.id
                         and newer.indexRow < :firstRow and newer.indexRow > r.indexRow)))
            order by r.indexRow, r.id
            """)
    List<TbReferenceItemEntity> findViewMarkers(@Param("periodId") Long periodId,
            @Param("enterpriseId") Long enterpriseId, @Param("referenceIds") List<Long> referenceIds,
            @Param("firstRow") Long firstRow, @Param("lastRow") Long lastRow);
}

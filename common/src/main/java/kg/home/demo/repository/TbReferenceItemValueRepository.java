package kg.home.demo.repository;

import kg.home.demo.entity.TbReferenceItemValueEntity;
import kg.home.demo.model.SubmittedIncomingReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TbReferenceItemValueRepository extends JpaRepository<TbReferenceItemValueEntity, Long> {
    @Query("""
            select case when count(v) > 0 then true else false end
            from TbReferenceItemValueEntity v
            join v.reportInReferenceItem.reportIn m
            where m.typePeriod.id = :typePeriodId and m.typeEnterprise.id = :typeEnterpriseId
              and v.referenceItem.reportInReference.reportIn.id = m.id
              and v.referenceItem.period.typePeriod.id = m.typePeriod.id
              and v.referenceItem.enterprise.typeEnterprise.id = m.typeEnterprise.id
            """)
    boolean existsSubmittedDataByTypes(@Param("typePeriodId") Long typePeriodId,
            @Param("typeEnterpriseId") Long typeEnterpriseId);

    // Header markers alone do not constitute a submitted report: at least one cell must exist.
    String SUBMISSIONS_FROM = """
            from TB_REFERENCE_ITEM_VALUE v
            join TB_REFERENCE_ITEM r on r.ID = v.REFERENCE_ITEM_ID
            join MD_REPORT_IN_REFERENCE mr on mr.ID = r.REPORT_IN_REFERENCE_ID
            join MD_REPORT_IN_REFERENCE_ITEM f on f.ID = v.REPORT_IN_REFERENCE_ITEM_ID
            join MD_REPORT_IN m on m.ID = mr.REPORT_IN_ID and m.ID = f.REPORT_IN_ID
            join DR_PERIOD p on p.ID = r.PERIOD_ID and p.TYPE_PERIOD_ID = m.TYPE_PERIOD_ID
            join DR_ENTERPRISE e on e.ID = r.ENTERPRISE_ID and e.TYPE_ENTERPRISE_ID = m.TYPE_ENTERPRISE_ID
            where m.TYPE_PERIOD_ID in (:typePeriodIds) and m.TYPE_ENTERPRISE_ID in (:typeEnterpriseIds)
              and (:search is null or instr(lower(p.NAME_PERIOD), :search) > 0
                   or instr(lower(e.NAME_ENTERPRISE), :search) > 0
                   or instr(lower(e.CODE_ENTERPRISE), :search) > 0)
            """;

    interface SubmissionRow {
        Long getPeriodId();
        String getPeriodName();
        Long getEnterpriseId();
        String getEnterpriseName();
        String getEnterpriseCode();
    }

    @Query(value = """
            select p.ID as periodId, p.NAME_PERIOD as periodName, e.ID as enterpriseId,
                   e.NAME_ENTERPRISE as enterpriseName, e.CODE_ENTERPRISE as enterpriseCode
            """ + SUBMISSIONS_FROM + """
            group by p.ID, p.NAME_PERIOD, p.DATE_BEGIN, e.ID, e.NAME_ENTERPRISE, e.CODE_ENTERPRISE, e.SORT_INDEX
            order by p.DATE_BEGIN desc, p.ID desc, e.SORT_INDEX, e.ID
            """, countQuery = "select count(*) from (select distinct p.ID as PERIOD_ID, e.ID as ENTERPRISE_ID " + SUBMISSIONS_FROM + ")",
            nativeQuery = true)
    Page<SubmissionRow> findSubmissions(@Param("typePeriodIds") List<Long> typePeriodIds,
            @Param("typeEnterpriseIds") List<Long> typeEnterpriseIds, @Param("search") String search, Pageable pageable);

    @Query("""
            select new kg.home.demo.model.SubmittedIncomingReport(m.id, m.codeReportIn, m.nameReportIn)
            from MdReportInEntity m
            where exists (
                select v.id from TbReferenceItemValueEntity v
                where v.reportInReferenceItem.reportIn.id = m.id
                  and v.referenceItem.reportInReference.reportIn.id = m.id
                  and v.referenceItem.period.id = :periodId and v.referenceItem.enterprise.id = :enterpriseId
                  and v.referenceItem.period.typePeriod.id = m.typePeriod.id
                  and v.referenceItem.enterprise.typeEnterprise.id = m.typeEnterprise.id)
            order by m.id
            """)
    Page<SubmittedIncomingReport> findSubmittedReports(@Param("periodId") Long periodId,
            @Param("enterpriseId") Long enterpriseId, Pageable pageable);

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

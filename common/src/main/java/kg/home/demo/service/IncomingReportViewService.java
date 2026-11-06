package kg.home.demo.service;

import kg.home.demo.model.IncomingReportView;
import kg.home.demo.model.IncomingReportSubmission;
import kg.home.demo.model.PageResult;
import kg.home.demo.model.SubmittedIncomingReport;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IncomingReportViewService {
    PageResult<IncomingReportSubmission> submissions(List<Long> typePeriodIds, List<Long> typeEnterpriseIds,
                                                    String search, Pageable pageable);
    PageResult<SubmittedIncomingReport> submittedReports(Long periodId, Long enterpriseId, Pageable pageable);
    IncomingReportView get(Long reportId, Long periodId, Long enterpriseId, Pageable pageable);
}

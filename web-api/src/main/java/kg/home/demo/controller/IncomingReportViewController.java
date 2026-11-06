package kg.home.demo.controller;

import kg.home.demo.controller.api.IncomingReportViewControllerApi;
import kg.home.demo.model.IncomingReportView;
import kg.home.demo.model.IncomingReportSubmission;
import kg.home.demo.model.PageResult;
import kg.home.demo.model.SubmittedIncomingReport;
import kg.home.demo.service.IncomingReportViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class IncomingReportViewController implements IncomingReportViewControllerApi {
    private final IncomingReportViewService service;

    @Override
    public ResponseEntity<PageResult<IncomingReportSubmission>> submissions(List<Long> typePeriodIds,
            List<Long> typeEnterpriseIds, String search, int page, int size) {
        return ResponseEntity.ok(service.submissions(typePeriodIds, typeEnterpriseIds, search, PageRequest.of(page, size)));
    }

    @Override
    public ResponseEntity<PageResult<SubmittedIncomingReport>> submittedReports(Long periodId, Long enterpriseId,
                                                                              int page, int size) {
        return ResponseEntity.ok(service.submittedReports(periodId, enterpriseId, PageRequest.of(page, size)));
    }

    @Override
    public ResponseEntity<IncomingReportView> get(Long id, Long periodId, Long enterpriseId, int page, int size) {
        return ResponseEntity.ok(service.get(id, periodId, enterpriseId, PageRequest.of(page, size)));
    }
}

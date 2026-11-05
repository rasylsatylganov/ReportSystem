package kg.home.demo.service;

import kg.home.demo.model.IncomingReportView;
import org.springframework.data.domain.Pageable;

public interface IncomingReportViewService {
    IncomingReportView get(Long reportId, Long periodId, Long enterpriseId, Pageable pageable);
}

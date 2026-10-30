package kg.home.demo.service;

import kg.home.demo.model.*;
import org.springframework.data.domain.Pageable;

public interface ReportTemplateService {
    PageResult<MdReportIn> findPage(Pageable pageable);
    ReportTemplate get(Long id);
    ReportDesignerLookups lookups();
    void validate(ReportTemplateRequest request);
    ReportTemplate create(ReportTemplateRequest request);
    ReportTemplate update(Long id, ReportTemplateRequest request);
    void delete(Long id);
}

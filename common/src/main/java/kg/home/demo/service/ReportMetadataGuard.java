package kg.home.demo.service;

import kg.home.demo.entity.MdReportInEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.exception.TemplateConflictException;
import kg.home.demo.model.ReportMetadataAccess;
import kg.home.demo.repository.MdReportInRepository;
import kg.home.demo.repository.TbReferenceItemValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportMetadataGuard {
    private static final String LOCK_REASON = "Редактирование метаданных недоступно: для этой пары " +
            "«Тип периода + Тип предприятия» уже есть сданные отчёты. Доступен только просмотр.";

    private final TbReferenceItemValueRepository values;
    private final MdReportInRepository reports;

    public ReportMetadataAccess access(Long typePeriodId, Long typeEnterpriseId) {
        boolean readOnly = values.existsSubmittedDataByTypes(typePeriodId, typeEnterpriseId);
        return new ReportMetadataAccess(readOnly, readOnly ? LOCK_REASON : null);
    }

    public void requireEditable(Long typePeriodId, Long typeEnterpriseId) {
        var access = access(typePeriodId, typeEnterpriseId);
        if (access.readOnly()) throw new TemplateConflictException(access.reason());
    }

    public void requireEditable(MdReportInEntity report) {
        requireEditable(report.getTypePeriod().getId(), report.getTypeEnterprise().getId());
    }

    public void requireEditableReport(Long reportId) {
        requireEditable(reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Входящий отчёт", reportId)));
    }
}

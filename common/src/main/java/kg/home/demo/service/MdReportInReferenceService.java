package kg.home.demo.service;

import kg.home.demo.entity.MdReportInReference;

import java.util.List;
import java.util.Optional;

public interface MdReportInReferenceService {

    List<MdReportInReference> getAllMdReportInReferences();

    Optional<MdReportInReference> getMdReportInReferenceById(Long id);

    MdReportInReference createMdReportInReference(MdReportInReference mdReportInReference);

    MdReportInReference updateMdReportInReference(Long id, MdReportInReference mdReportInReference);

    void deleteMdReportInReference(Long id);
}

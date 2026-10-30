package kg.home.demo.service;

import kg.home.demo.model.MdReportInReference;
import kg.home.demo.model.MdReportInReferenceRequest;

import java.util.List;
import java.util.Optional;

public interface MdReportInReferenceService {

    List<MdReportInReference> getAllMdReportInReferences();

    Optional<MdReportInReference> getMdReportInReferenceById(Long id);

    MdReportInReference createMdReportInReference(MdReportInReferenceRequest request);

    MdReportInReference updateMdReportInReference(Long id, MdReportInReferenceRequest request);

    void deleteMdReportInReference(Long id);
}

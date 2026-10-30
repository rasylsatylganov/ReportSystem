package kg.home.demo.service;

import kg.home.demo.entity.MdReportInReferenceEntity;

import java.util.List;
import java.util.Optional;

public interface MdReportInReferenceService {

    List<MdReportInReferenceEntity> getAllMdReportInReferences();

    Optional<MdReportInReferenceEntity> getMdReportInReferenceById(Long id);

    MdReportInReferenceEntity createMdReportInReference(MdReportInReferenceEntity mdReportInReference);

    MdReportInReferenceEntity updateMdReportInReference(Long id, MdReportInReferenceEntity mdReportInReference);

    void deleteMdReportInReference(Long id);
}

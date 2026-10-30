package kg.home.demo.service;

import kg.home.demo.entity.MdReportInReferenceItemEntity;

import java.util.List;
import java.util.Optional;

public interface MdReportInReferenceItemService {

    List<MdReportInReferenceItemEntity> getAllMdReportInReferenceItems();

    Optional<MdReportInReferenceItemEntity> getMdReportInReferenceItemById(Long id);

    MdReportInReferenceItemEntity createMdReportInReferenceItem(MdReportInReferenceItemEntity mdReportInReferenceItem);

    MdReportInReferenceItemEntity updateMdReportInReferenceItem(Long id, MdReportInReferenceItemEntity mdReportInReferenceItem);

    void deleteMdReportInReferenceItem(Long id);
}

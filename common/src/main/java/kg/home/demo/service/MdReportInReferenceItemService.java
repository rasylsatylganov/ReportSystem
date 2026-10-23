package kg.home.demo.service;

import kg.home.demo.entity.MdReportInReferenceItem;

import java.util.List;
import java.util.Optional;

public interface MdReportInReferenceItemService {

    List<MdReportInReferenceItem> getAllMdReportInReferenceItems();

    Optional<MdReportInReferenceItem> getMdReportInReferenceItemById(Long id);

    MdReportInReferenceItem createMdReportInReferenceItem(MdReportInReferenceItem mdReportInReferenceItem);

    MdReportInReferenceItem updateMdReportInReferenceItem(Long id, MdReportInReferenceItem mdReportInReferenceItem);

    void deleteMdReportInReferenceItem(Long id);
}

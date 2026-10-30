package kg.home.demo.service;

import kg.home.demo.model.MdReportInReferenceItem;
import kg.home.demo.model.MdReportInReferenceItemRequest;

import java.util.List;
import java.util.Optional;

public interface MdReportInReferenceItemService {

    List<MdReportInReferenceItem> getAllMdReportInReferenceItems();

    Optional<MdReportInReferenceItem> getMdReportInReferenceItemById(Long id);

    MdReportInReferenceItem createMdReportInReferenceItem(MdReportInReferenceItemRequest request);

    MdReportInReferenceItem updateMdReportInReferenceItem(Long id, MdReportInReferenceItemRequest request);

    void deleteMdReportInReferenceItem(Long id);
}

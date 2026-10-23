package kg.home.demo.service;

import kg.home.demo.entity.DrReferenceItem;

import java.util.List;
import java.util.Optional;

public interface DrReferenceItemService {

    List<DrReferenceItem> getAllDrReferenceItems();

    Optional<DrReferenceItem> getDrReferenceItemById(Long id);

    DrReferenceItem createDrReferenceItem(DrReferenceItem drReferenceItem);

    DrReferenceItem updateDrReferenceItem(Long id, DrReferenceItem drReferenceItem);

    void deleteDrReferenceItem(Long id);
}

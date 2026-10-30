package kg.home.demo.service;

import kg.home.demo.model.DrReferenceItem;
import kg.home.demo.model.DrReferenceItemRequest;

import java.util.List;
import java.util.Optional;

public interface DrReferenceItemService {

    List<DrReferenceItem> findAllDrReferenceItems();

    Optional<DrReferenceItem> findDrReferenceItemById(Long id);

    DrReferenceItem createDrReferenceItem(DrReferenceItemRequest request);

    DrReferenceItem updateDrReferenceItem(Long id, DrReferenceItemRequest request);

    void deleteDrReferenceItem(Long id);
}

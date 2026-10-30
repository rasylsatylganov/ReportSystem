package kg.home.demo.service;

import kg.home.demo.model.DrReferenceItem;
import kg.home.demo.model.DrReferenceItemRequest;
import kg.home.demo.model.PageResult;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DrReferenceItemService {

    PageResult<DrReferenceItem> findAllDrReferenceItems(Pageable pageable);

    Optional<DrReferenceItem> findDrReferenceItemById(Long id);

    DrReferenceItem createDrReferenceItem(DrReferenceItemRequest request);

    DrReferenceItem updateDrReferenceItem(Long id, DrReferenceItemRequest request);

    void deleteDrReferenceItem(Long id);
}

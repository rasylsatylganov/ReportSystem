package kg.home.demo.service;

import kg.home.demo.entity.DrReferenceItemEntity;

import java.util.List;
import java.util.Optional;

public interface DrReferenceItemService {

    List<DrReferenceItemEntity> getAllDrReferenceItems();

    Optional<DrReferenceItemEntity> getDrReferenceItemById(Long id);

    DrReferenceItemEntity createDrReferenceItem(DrReferenceItemEntity drReferenceItem);

    DrReferenceItemEntity updateDrReferenceItem(Long id, DrReferenceItemEntity drReferenceItem);

    void deleteDrReferenceItem(Long id);
}

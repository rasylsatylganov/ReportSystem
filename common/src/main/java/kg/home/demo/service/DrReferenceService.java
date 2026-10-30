package kg.home.demo.service;

import kg.home.demo.entity.DrReferenceEntity;

import java.util.List;
import java.util.Optional;

public interface DrReferenceService {

    List<DrReferenceEntity> getAllDrReferences();

    Optional<DrReferenceEntity> getDrReferenceById(Long id);

    DrReferenceEntity createDrReference(DrReferenceEntity drReference);

    DrReferenceEntity updateDrReference(Long id, DrReferenceEntity drReference);

    void deleteDrReference(Long id);
}

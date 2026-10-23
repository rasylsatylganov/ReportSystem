package kg.home.demo.service;

import kg.home.demo.entity.DrReference;

import java.util.List;
import java.util.Optional;

public interface DrReferenceService {

    List<DrReference> getAllDrReferences();

    Optional<DrReference> getDrReferenceById(Long id);

    DrReference createDrReference(DrReference drReference);

    DrReference updateDrReference(Long id, DrReference drReference);

    void deleteDrReference(Long id);
}

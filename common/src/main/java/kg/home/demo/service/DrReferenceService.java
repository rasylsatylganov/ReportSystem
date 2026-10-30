package kg.home.demo.service;

import kg.home.demo.model.DrReference;
import kg.home.demo.model.DrReferenceRequest;

import java.util.List;
import java.util.Optional;

public interface DrReferenceService {

    List<DrReference> getAllDrReferences();

    Optional<DrReference> getDrReferenceById(Long id);

    DrReference createDrReference(DrReferenceRequest request);

    DrReference updateDrReference(Long id, DrReferenceRequest request);

    void deleteDrReference(Long id);
}

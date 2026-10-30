package kg.home.demo.service;

import kg.home.demo.model.DrReference;
import kg.home.demo.model.DrReferenceRequest;
import kg.home.demo.model.PageResult;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DrReferenceService {

    PageResult<DrReference> findAllDrReferences(Pageable pageable);

    Optional<DrReference> findDrReferenceById(Long id);

    DrReference createDrReference(DrReferenceRequest request);

    DrReference updateDrReference(Long id, DrReferenceRequest request);

    void deleteDrReference(Long id);

    void moveUp(Long id);

    void moveDown(Long id);
}

package kg.home.demo.service;

import kg.home.demo.model.DrTypePosition;
import kg.home.demo.model.DrTypePositionRequest;

import java.util.List;
import java.util.Optional;

public interface DrTypePositionService {

    List<DrTypePosition> getAllDrTypePositions();

    Optional<DrTypePosition> getDrTypePositionById(Long id);

    DrTypePosition createDrTypePosition(DrTypePositionRequest request);

    DrTypePosition updateDrTypePosition(Long id, DrTypePositionRequest request);

    void deleteDrTypePosition(Long id);
}

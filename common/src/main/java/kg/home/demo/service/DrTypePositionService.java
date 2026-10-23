package kg.home.demo.service;

import kg.home.demo.entity.DrTypePosition;

import java.util.List;
import java.util.Optional;

public interface DrTypePositionService {

    List<DrTypePosition> getAllDrTypePositions();

    Optional<DrTypePosition> getDrTypePositionById(Long id);

    DrTypePosition createDrTypePosition(DrTypePosition drTypePosition);

    DrTypePosition updateDrTypePosition(Long id, DrTypePosition drTypePosition);

    void deleteDrTypePosition(Long id);
}

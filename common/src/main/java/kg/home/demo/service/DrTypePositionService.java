package kg.home.demo.service;

import kg.home.demo.entity.DrTypePositionEntity;

import java.util.List;
import java.util.Optional;

public interface DrTypePositionService {

    List<DrTypePositionEntity> getAllDrTypePositions();

    Optional<DrTypePositionEntity> getDrTypePositionById(Long id);

    DrTypePositionEntity createDrTypePosition(DrTypePositionEntity drTypePosition);

    DrTypePositionEntity updateDrTypePosition(Long id, DrTypePositionEntity drTypePosition);

    void deleteDrTypePosition(Long id);
}

package kg.home.demo.service;

import kg.home.demo.entity.DrTypePeriodEntity;

import java.util.List;
import java.util.Optional;

public interface DrTypePeriodService {
    List<DrTypePeriodEntity> getAllDrTypePeriods();
    Optional<DrTypePeriodEntity> getDrTypePeriodById(Long id);
    DrTypePeriodEntity createDrTypePeriod(DrTypePeriodEntity drTypePeriod);
    DrTypePeriodEntity updateDrTypePeriod(Long id, DrTypePeriodEntity drTypePeriod);
    void deleteDrTypePeriod(Long id);
    void moveUp(Long id);
    void moveDown(Long id);
}

package kg.home.demo.service;

import kg.home.demo.entity.DrPeriodEntity;

import java.util.List;
import java.util.Optional;

public interface DrPeriodService {

    List<DrPeriodEntity> getAllDrPeriods();

    Optional<DrPeriodEntity> getDrPeriodById(Long id);

    DrPeriodEntity createDrPeriod(DrPeriodEntity drPeriod);

    DrPeriodEntity updateDrPeriod(Long id, DrPeriodEntity drPeriod);

    void deleteDrPeriod(Long id);
}

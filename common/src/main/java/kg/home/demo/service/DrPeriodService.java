package kg.home.demo.service;

import kg.home.demo.entity.DrPeriod;

import java.util.List;
import java.util.Optional;

public interface DrPeriodService {

    List<DrPeriod> getAllDrPeriods();

    Optional<DrPeriod> getDrPeriodById(Long id);

    DrPeriod createDrPeriod(DrPeriod drPeriod);

    DrPeriod updateDrPeriod(Long id, DrPeriod drPeriod);

    void deleteDrPeriod(Long id);
}

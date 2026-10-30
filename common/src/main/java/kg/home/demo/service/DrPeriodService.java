package kg.home.demo.service;

import kg.home.demo.model.DrPeriod;
import kg.home.demo.model.DrPeriodRequest;

import java.util.List;
import java.util.Optional;

public interface DrPeriodService {

    List<DrPeriod> getAllDrPeriods();

    Optional<DrPeriod> getDrPeriodById(Long id);

    DrPeriod createDrPeriod(DrPeriodRequest request);

    DrPeriod updateDrPeriod(Long id, DrPeriodRequest request);

    void deleteDrPeriod(Long id);
}

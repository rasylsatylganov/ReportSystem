package kg.home.demo.service;

import kg.home.demo.entity.DrTypePeriod;

import java.util.List;
import java.util.Optional;

public interface DrTypePeriodService {

    List<DrTypePeriod> getAllDrTypePeriods();

    Optional<DrTypePeriod> getDrTypePeriodById(Long id);

    DrTypePeriod createDrTypePeriod(DrTypePeriod drTypePeriod);

    DrTypePeriod updateDrTypePeriod(Long id, DrTypePeriod drTypePeriod);

    void deleteDrTypePeriod(Long id);
}

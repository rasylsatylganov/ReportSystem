package kg.home.demo.service;

import kg.home.demo.model.DrTypePeriod;
import kg.home.demo.model.DrTypePeriodRequest;

import java.util.List;
import java.util.Optional;

public interface DrTypePeriodService {

    List<DrTypePeriod> findAllDrTypePeriods();

    Optional<DrTypePeriod> findDrTypePeriodById(Long id);

    DrTypePeriod createDrTypePeriod(DrTypePeriodRequest request);

    DrTypePeriod updateDrTypePeriod(Long id, DrTypePeriodRequest request);

    void deleteDrTypePeriod(Long id);

    void moveUp(Long id);

    void moveDown(Long id);
}

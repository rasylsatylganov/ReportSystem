package kg.home.demo.service;

import kg.home.demo.model.DrPeriod;
import kg.home.demo.model.DrPeriodRequest;
import kg.home.demo.model.PageResult;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DrPeriodService {

    PageResult<DrPeriod> findAllDrPeriods(Pageable pageable);

    Optional<DrPeriod> findDrPeriodById(Long id);

    DrPeriod createDrPeriod(DrPeriodRequest request);

    DrPeriod updateDrPeriod(Long id, DrPeriodRequest request);

    void deleteDrPeriod(Long id);
}

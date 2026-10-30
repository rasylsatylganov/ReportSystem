package kg.home.demo.controller;

import kg.home.demo.controller.api.DrPeriodControllerApi;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrPeriod;
import kg.home.demo.model.DrPeriodRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.service.DrPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DrPeriodController implements DrPeriodControllerApi {


    private final DrPeriodService drPeriodService;

    @Override
    public ResponseEntity<PageResult<DrPeriod>> findAllDrPeriods(
            int page,
            int size) {

        return ResponseEntity.ok(
                drPeriodService.findAllDrPeriods(PageRequest.of(page, size, Sort.by("id"))));
    }

    @Override
    public ResponseEntity<DrPeriod> findDrPeriodById(Long id) {
        return ResponseEntity.ok(drPeriodService.findDrPeriodById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Период", id)));
    }

    @Override
    public ResponseEntity<DrPeriod> createDrPeriod(DrPeriodRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drPeriodService.createDrPeriod(request));
    }

    @Override
    public ResponseEntity<DrPeriod> updateDrPeriod(
            Long id, DrPeriodRequest request) {

        return ResponseEntity.ok(drPeriodService.updateDrPeriod(id, request));
    }

    @Override
    public ResponseEntity<Void> deleteDrPeriod(Long id) {
        drPeriodService.deleteDrPeriod(id);
        return ResponseEntity.noContent().build();
    }
}

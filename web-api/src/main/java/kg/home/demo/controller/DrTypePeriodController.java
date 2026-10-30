package kg.home.demo.controller;

import kg.home.demo.controller.api.DrTypePeriodControllerApi;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrTypePeriod;
import kg.home.demo.model.DrTypePeriodRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.service.DrTypePeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DrTypePeriodController implements DrTypePeriodControllerApi {


    private final DrTypePeriodService drTypePeriodService;

    @Override
    public ResponseEntity<PageResult<DrTypePeriod>> findAllDrTypePeriods(
            int page,
            int size) {

        return ResponseEntity.ok(
                drTypePeriodService.findAllDrTypePeriods(PageRequest.of(page, size, Sort.by("sortIndex", "id"))));
    }

    @Override
    public ResponseEntity<DrTypePeriod> findDrTypePeriodById(Long id) {
        return ResponseEntity.ok(drTypePeriodService.findDrTypePeriodById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тип периода", id)));
    }

    @Override
    public ResponseEntity<DrTypePeriod> createDrTypePeriod(DrTypePeriodRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drTypePeriodService.createDrTypePeriod(request));
    }

    @Override
    public ResponseEntity<DrTypePeriod> updateDrTypePeriod(
            Long id, DrTypePeriodRequest request) {

        return ResponseEntity.ok(drTypePeriodService.updateDrTypePeriod(id, request));
    }

    @Override
    public ResponseEntity<Void> deleteDrTypePeriod(Long id) {
        drTypePeriodService.deleteDrTypePeriod(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> moveUp(Long id) {
        drTypePeriodService.moveUp(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> moveDown(Long id) {
        drTypePeriodService.moveDown(id);
        return ResponseEntity.noContent().build();
    }
}

package kg.home.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dr-periods")
public class DrPeriodController {


    private final DrPeriodService drPeriodService;

    @GetMapping
    public ResponseEntity<PageResult<DrPeriod>> findAllDrPeriods(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        return ResponseEntity.ok(
                drPeriodService.findAllDrPeriods(PageRequest.of(page, size, Sort.by("id"))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrPeriod> findDrPeriodById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(drPeriodService.findDrPeriodById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Период", id)));
    }

    @PostMapping
    public ResponseEntity<DrPeriod> createDrPeriod(@Valid @RequestBody DrPeriodRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drPeriodService.createDrPeriod(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrPeriod> updateDrPeriod(
            @PathVariable("id") @Positive Long id, @Valid @RequestBody DrPeriodRequest request) {

        return ResponseEntity.ok(drPeriodService.updateDrPeriod(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrPeriod(@PathVariable("id") @Positive Long id) {
        drPeriodService.deleteDrPeriod(id);
        return ResponseEntity.noContent().build();
    }
}

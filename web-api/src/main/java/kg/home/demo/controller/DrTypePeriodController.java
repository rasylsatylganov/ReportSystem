package kg.home.demo.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dr-type-periods")
public class DrTypePeriodController {


    private final DrTypePeriodService drTypePeriodService;

    @GetMapping
    public ResponseEntity<PageResult<DrTypePeriod>> findAllDrTypePeriods(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        return ResponseEntity.ok(
                drTypePeriodService.findAllDrTypePeriods(PageRequest.of(page, size, Sort.by("sortIndex", "id"))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrTypePeriod> findDrTypePeriodById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(drTypePeriodService.findDrTypePeriodById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тип периода", id)));
    }

    @PostMapping
    public ResponseEntity<DrTypePeriod> createDrTypePeriod(@Valid @RequestBody DrTypePeriodRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drTypePeriodService.createDrTypePeriod(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrTypePeriod> updateDrTypePeriod(
            @PathVariable("id") @Positive Long id, @Valid @RequestBody DrTypePeriodRequest request) {

        return ResponseEntity.ok(drTypePeriodService.updateDrTypePeriod(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrTypePeriod(@PathVariable("id") @Positive Long id) {
        drTypePeriodService.deleteDrTypePeriod(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/move-up")
    public ResponseEntity<Void> moveUp(@PathVariable("id") @Positive Long id) {
        drTypePeriodService.moveUp(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/move-down")
    public ResponseEntity<Void> moveDown(@PathVariable("id") @Positive Long id) {
        drTypePeriodService.moveDown(id);
        return ResponseEntity.noContent().build();
    }
}

package kg.home.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrPeriod;
import kg.home.demo.model.DrPeriodRequest;
import kg.home.demo.service.DrPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dr-periods")
public class DrPeriodController {


    private final DrPeriodService drPeriodService;

    @GetMapping
    public ResponseEntity<List<DrPeriod>> findAllDrPeriods() {
        return ResponseEntity.ok(drPeriodService.findAllDrPeriods());
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

package kg.home.demo.controller;

import kg.home.demo.entity.DrTypePeriod;
import kg.home.demo.service.DrTypePeriodService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dr-type-periods")
public class DrTypePeriodController {

    private final DrTypePeriodService drTypePeriodService;

    public DrTypePeriodController(DrTypePeriodService drTypePeriodService) {
        this.drTypePeriodService = drTypePeriodService;
    }

    // Получить все типы периодов
    @GetMapping
    public ResponseEntity<List<DrTypePeriod>> getAllDrTypePeriods() {
        return ResponseEntity.ok(
                drTypePeriodService.getAllDrTypePeriods()
        );
    }

    // Получить тип периода по ID
    @GetMapping("/{id}")
    public ResponseEntity<DrTypePeriod> getDrTypePeriodById(
            @PathVariable("id") Long id
    ) {
        return drTypePeriodService.getDrTypePeriodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создать тип периода
    @PostMapping
    public ResponseEntity<DrTypePeriod> createDrTypePeriod(
            @RequestBody DrTypePeriod drTypePeriod
    ) {
        drTypePeriod.setId(null);

        DrTypePeriod saved = drTypePeriodService.createDrTypePeriod(
                drTypePeriod
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Обновить тип периода
    @PutMapping("/{id}")
    public ResponseEntity<DrTypePeriod> updateDrTypePeriod(
            @PathVariable("id") Long id,
            @RequestBody DrTypePeriod drTypePeriod
    ) {
        try {
            DrTypePeriod updated = drTypePeriodService.updateDrTypePeriod(
                    id, drTypePeriod
            );

            return ResponseEntity.ok(updated);
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Удалить тип периода
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrTypePeriod(
            @PathVariable("id") Long id
    ) {
        try {
            drTypePeriodService.deleteDrTypePeriod(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
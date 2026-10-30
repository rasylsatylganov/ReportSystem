package kg.home.demo.controller;

import kg.home.demo.entity.DrPeriodEntity;
import kg.home.demo.service.DrPeriodService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dr-periods")
public class DrPeriodController {

    private final DrPeriodService drPeriodService;

    public DrPeriodController(DrPeriodService drPeriodService) {
        this.drPeriodService = drPeriodService;
    }

    // Получить все периоды
    @GetMapping
    public ResponseEntity<List<DrPeriodEntity>> getAllDrPeriods() {
        return ResponseEntity.ok(
                drPeriodService.getAllDrPeriods()
        );
    }

    // Получить период по ID
    @GetMapping("/{id}")
    public ResponseEntity<DrPeriodEntity> getDrPeriodById(
            @PathVariable("id") Long id
    ) {
        return drPeriodService.getDrPeriodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создать период
    @PostMapping
    public ResponseEntity<DrPeriodEntity> createDrPeriod(
            @RequestBody DrPeriodEntity drPeriod
    ) {
        drPeriod.setId(null);

        DrPeriodEntity saved = drPeriodService.createDrPeriod(drPeriod);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Обновить период
    @PutMapping("/{id}")
    public ResponseEntity<DrPeriodEntity> updateDrPeriod(
            @PathVariable("id") Long id,
            @RequestBody DrPeriodEntity drPeriod
    ) {
        try {
            DrPeriodEntity updated = drPeriodService.updateDrPeriod(id, drPeriod);

            return ResponseEntity.ok(updated);
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Удалить период
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrPeriod(
            @PathVariable("id") Long id
    ) {
        try {
            drPeriodService.deleteDrPeriod(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
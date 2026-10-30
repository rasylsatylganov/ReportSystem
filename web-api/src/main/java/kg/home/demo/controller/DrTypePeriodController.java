package kg.home.demo.controller;

import jakarta.persistence.EntityNotFoundException;
import kg.home.demo.entity.DrTypePeriodEntity;
import kg.home.demo.service.DrTypePeriodService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dr-type-periods")
public class DrTypePeriodController {

    private final DrTypePeriodService service;

    public DrTypePeriodController(DrTypePeriodService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DrTypePeriodEntity>> getAllDrTypePeriods() {
        return ResponseEntity.ok(service.getAllDrTypePeriods());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrTypePeriodEntity> getDrTypePeriodById(@PathVariable("id") Long id) {
        return service.getDrTypePeriodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DrTypePeriodEntity> createDrTypePeriod(@RequestBody DrTypePeriodEntity period) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDrTypePeriod(period));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrTypePeriodEntity> updateDrTypePeriod(
            @PathVariable("id") Long id, @RequestBody DrTypePeriodEntity period) {
        return ResponseEntity.ok(service.updateDrTypePeriod(id, period));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrTypePeriod(@PathVariable("id") Long id) {
        service.deleteDrTypePeriod(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/move-up")
    public ResponseEntity<Void> moveUp(@PathVariable("id") Long id) {
        service.moveUp(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/move-down")
    public ResponseEntity<Void> moveDown(@PathVariable("id") Long id) {
        service.moveDown(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalid(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraint(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message",
                "Операция нарушает ограничение БД: код уже существует или запись используется в связанных таблицах."));
    }
}

package kg.home.demo.controller;

import kg.home.demo.entity.DrReferenceEntity;
import kg.home.demo.service.DrReferenceService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dr-references")
public class DrReferenceController {

    private final DrReferenceService drReferenceService;

    public DrReferenceController(DrReferenceService drReferenceService) {
        this.drReferenceService = drReferenceService;
    }

    // Получить все справочники
    @GetMapping
    public ResponseEntity<List<DrReferenceEntity>> getAllDrReferences() {
        return ResponseEntity.ok(
                drReferenceService.getAllDrReferences()
        );
    }

    // Получить справочник по ID
    @GetMapping("/{id}")
    public ResponseEntity<DrReferenceEntity> getDrReferenceById(
            @PathVariable("id") Long id
    ) {
        return drReferenceService.getDrReferenceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создать справочник
    @PostMapping
    public ResponseEntity<DrReferenceEntity> createDrReference(
            @RequestBody DrReferenceEntity drReference
    ) {
        drReference.setId(null);

        DrReferenceEntity saved = drReferenceService.createDrReference(drReference);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Обновить справочник
    @PutMapping("/{id}")
    public ResponseEntity<DrReferenceEntity> updateDrReference(
            @PathVariable("id") Long id,
            @RequestBody DrReferenceEntity drReference
    ) {
        try {
            DrReferenceEntity updated =
                    drReferenceService.updateDrReference(id, drReference);

            return ResponseEntity.ok(updated);
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Удалить справочник
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrReference(
            @PathVariable("id") Long id
    ) {
        try {
            drReferenceService.deleteDrReference(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
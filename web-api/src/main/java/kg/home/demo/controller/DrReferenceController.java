package kg.home.demo.controller;

import kg.home.demo.model.DrReference;
import kg.home.demo.model.DrReferenceRequest;
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
    public ResponseEntity<List<DrReference>> getAllDrReferences() {
        return ResponseEntity.ok(
                drReferenceService.getAllDrReferences()
        );
    }

    // Получить справочник по ID
    @GetMapping("/{id}")
    public ResponseEntity<DrReference> getDrReferenceById(
            @PathVariable("id") Long id
    ) {
        return drReferenceService.getDrReferenceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создать справочник
    @PostMapping
    public ResponseEntity<DrReference> createDrReference(
            @RequestBody DrReferenceRequest drReference
    ) {

        DrReference saved = drReferenceService.createDrReference(drReference);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Обновить справочник
    @PutMapping("/{id}")
    public ResponseEntity<DrReference> updateDrReference(
            @PathVariable("id") Long id,
            @RequestBody DrReferenceRequest drReference
    ) {
        try {
            DrReference updated =
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
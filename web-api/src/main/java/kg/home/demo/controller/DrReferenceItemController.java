package kg.home.demo.controller;

import kg.home.demo.entity.DrReferenceItem;
import kg.home.demo.service.DrReferenceItemService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dr-reference-items")
public class DrReferenceItemController {

    private final DrReferenceItemService drReferenceItemService;

    public DrReferenceItemController(
            DrReferenceItemService drReferenceItemService
    ) {
        this.drReferenceItemService = drReferenceItemService;
    }

    // Получить все элементы справочников
    @GetMapping
    public ResponseEntity<List<DrReferenceItem>> getAllDrReferenceItems() {
        return ResponseEntity.ok(
                drReferenceItemService.getAllDrReferenceItems()
        );
    }

    // Получить элемент справочника по ID
    @GetMapping("/{id}")
    public ResponseEntity<DrReferenceItem> getDrReferenceItemById(
            @PathVariable("id") Long id
    ) {
        return drReferenceItemService.getDrReferenceItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создать элемент справочника
    @PostMapping
    public ResponseEntity<DrReferenceItem> createDrReferenceItem(
            @RequestBody DrReferenceItem drReferenceItem
    ) {
        drReferenceItem.setId(null);

        DrReferenceItem saved =
                drReferenceItemService.createDrReferenceItem(drReferenceItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Обновить элемент справочника
    @PutMapping("/{id}")
    public ResponseEntity<DrReferenceItem> updateDrReferenceItem(
            @PathVariable("id") Long id,
            @RequestBody DrReferenceItem drReferenceItem
    ) {
        try {
            DrReferenceItem updated =
                    drReferenceItemService.updateDrReferenceItem(
                            id, drReferenceItem
                    );

            return ResponseEntity.ok(updated);
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Удалить элемент справочника
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrReferenceItem(
            @PathVariable("id") Long id
    ) {
        try {
            drReferenceItemService.deleteDrReferenceItem(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
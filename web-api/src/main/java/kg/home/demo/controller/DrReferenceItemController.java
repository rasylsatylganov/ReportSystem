package kg.home.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrReferenceItem;
import kg.home.demo.model.DrReferenceItemRequest;
import kg.home.demo.service.DrReferenceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dr-reference-items")
public class DrReferenceItemController {


    private final DrReferenceItemService drReferenceItemService;

    @GetMapping
    public ResponseEntity<List<DrReferenceItem>> findAllDrReferenceItems() {
        return ResponseEntity.ok(drReferenceItemService.findAllDrReferenceItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrReferenceItem> findDrReferenceItemById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(drReferenceItemService.findDrReferenceItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Элемент справочника", id)));
    }

    @PostMapping
    public ResponseEntity<DrReferenceItem> createDrReferenceItem(@Valid @RequestBody DrReferenceItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drReferenceItemService.createDrReferenceItem(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrReferenceItem> updateDrReferenceItem(
            @PathVariable("id") @Positive Long id, @Valid @RequestBody DrReferenceItemRequest request) {

        return ResponseEntity.ok(drReferenceItemService.updateDrReferenceItem(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrReferenceItem(@PathVariable("id") @Positive Long id) {
        drReferenceItemService.deleteDrReferenceItem(id);
        return ResponseEntity.noContent().build();
    }
}

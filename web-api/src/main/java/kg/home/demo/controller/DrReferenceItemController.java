package kg.home.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrReferenceItem;
import kg.home.demo.model.DrReferenceItemRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.service.DrReferenceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dr-reference-items")
public class DrReferenceItemController {


    private final DrReferenceItemService drReferenceItemService;

    @GetMapping
    public ResponseEntity<PageResult<DrReferenceItem>> findAllDrReferenceItems(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        return ResponseEntity.ok(
                drReferenceItemService.findAllDrReferenceItems(PageRequest.of(page, size, Sort.by("id"))));
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

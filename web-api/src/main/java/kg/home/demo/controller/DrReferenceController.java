package kg.home.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrReference;
import kg.home.demo.model.DrReferenceRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.service.DrReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dr-references")
public class DrReferenceController {


    private final DrReferenceService drReferenceService;

    @GetMapping
    public ResponseEntity<PageResult<DrReference>> findAllDrReferences(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        return ResponseEntity.ok(
                drReferenceService.findAllDrReferences(PageRequest.of(page, size, Sort.by("id"))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrReference> findDrReferenceById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(drReferenceService.findDrReferenceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Справочник", id)));
    }

    @PostMapping
    public ResponseEntity<DrReference> createDrReference(@Valid @RequestBody DrReferenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drReferenceService.createDrReference(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrReference> updateDrReference(
            @PathVariable("id") @Positive Long id, @Valid @RequestBody DrReferenceRequest request) {

        return ResponseEntity.ok(drReferenceService.updateDrReference(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrReference(@PathVariable("id") @Positive Long id) {
        drReferenceService.deleteDrReference(id);
        return ResponseEntity.noContent().build();
    }
}

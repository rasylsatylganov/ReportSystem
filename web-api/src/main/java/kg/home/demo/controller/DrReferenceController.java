package kg.home.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrReference;
import kg.home.demo.model.DrReferenceRequest;
import kg.home.demo.service.DrReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dr-references")
public class DrReferenceController {


    private final DrReferenceService drReferenceService;

    @GetMapping
    public ResponseEntity<List<DrReference>> getAllDrReferences() {
        return ResponseEntity.ok(drReferenceService.getAllDrReferences());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrReference> getDrReferenceById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(drReferenceService.getDrReferenceById(id)
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

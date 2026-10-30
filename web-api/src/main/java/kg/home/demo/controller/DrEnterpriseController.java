package kg.home.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrEnterprise;
import kg.home.demo.model.DrEnterpriseRequest;
import kg.home.demo.service.DrEnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dr-enterprises")
public class DrEnterpriseController {


    private final DrEnterpriseService drEnterpriseService;

    @GetMapping
    public ResponseEntity<List<DrEnterprise>> getAllDrEnterprises() {
        return ResponseEntity.ok(drEnterpriseService.getAllDrEnterprises());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrEnterprise> getDrEnterpriseById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(drEnterpriseService.getDrEnterpriseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Предприятие", id)));
    }

    @PostMapping
    public ResponseEntity<DrEnterprise> createDrEnterprise(@Valid @RequestBody DrEnterpriseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drEnterpriseService.createDrEnterprise(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrEnterprise> updateDrEnterprise(
            @PathVariable("id") @Positive Long id, @Valid @RequestBody DrEnterpriseRequest request) {

        return ResponseEntity.ok(drEnterpriseService.updateDrEnterprise(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrEnterprise(@PathVariable("id") @Positive Long id) {
        drEnterpriseService.deleteDrEnterprise(id);
        return ResponseEntity.noContent().build();
    }
}

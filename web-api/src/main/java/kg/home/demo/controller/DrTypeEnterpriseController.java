package kg.home.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrTypeEnterprise;
import kg.home.demo.model.DrTypeEnterpriseRequest;
import kg.home.demo.service.DrTypeEnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dr-type-enterprises")
public class DrTypeEnterpriseController {


    private final DrTypeEnterpriseService drTypeEnterpriseService;

    @GetMapping
    public ResponseEntity<List<DrTypeEnterprise>> getAllDrTypeEnterprises() {
        return ResponseEntity.ok(drTypeEnterpriseService.getAllDrTypeEnterprises());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrTypeEnterprise> getDrTypeEnterpriseById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(drTypeEnterpriseService.getDrTypeEnterpriseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тип предприятия", id)));
    }

    @PostMapping
    public ResponseEntity<DrTypeEnterprise> createDrTypeEnterprise(
            @Valid @RequestBody DrTypeEnterpriseRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(drTypeEnterpriseService.createDrTypeEnterprise(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrTypeEnterprise> updateDrTypeEnterprise(
            @PathVariable("id") @Positive Long id, @Valid @RequestBody DrTypeEnterpriseRequest request) {

        return ResponseEntity.ok(drTypeEnterpriseService.updateDrTypeEnterprise(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrTypeEnterprise(@PathVariable("id") @Positive Long id) {
        drTypeEnterpriseService.deleteDrTypeEnterprise(id);
        return ResponseEntity.noContent().build();
    }
}

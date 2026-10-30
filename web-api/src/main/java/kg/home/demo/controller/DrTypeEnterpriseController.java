package kg.home.demo.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrTypeEnterprise;
import kg.home.demo.model.DrTypeEnterpriseRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.service.DrTypeEnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dr-type-enterprises")
public class DrTypeEnterpriseController {


    private final DrTypeEnterpriseService drTypeEnterpriseService;

    @GetMapping
    public ResponseEntity<PageResult<DrTypeEnterprise>> findAllDrTypeEnterprises(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        return ResponseEntity.ok(
                drTypeEnterpriseService.findAllDrTypeEnterprises(PageRequest.of(page, size, Sort.by("id"))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrTypeEnterprise> findDrTypeEnterpriseById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(drTypeEnterpriseService.findDrTypeEnterpriseById(id)
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

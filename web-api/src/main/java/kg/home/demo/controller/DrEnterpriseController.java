package kg.home.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrEnterprise;
import kg.home.demo.model.DrEnterpriseRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.service.DrEnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dr-enterprises")
public class DrEnterpriseController {


    private final DrEnterpriseService drEnterpriseService;

    @GetMapping
    public ResponseEntity<PageResult<DrEnterprise>> findAllDrEnterprises(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        return ResponseEntity.ok(
                drEnterpriseService.findAllDrEnterprises(PageRequest.of(page, size, Sort.by("id"))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrEnterprise> findDrEnterpriseById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(drEnterpriseService.findDrEnterpriseById(id)
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

package kg.home.demo.controller;

import kg.home.demo.model.DrEnterprise;
import kg.home.demo.model.DrEnterpriseRequest;
import kg.home.demo.service.DrEnterpriseService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dr-enterprises")
public class DrEnterpriseController {

    private final DrEnterpriseService drEnterpriseService;

    public DrEnterpriseController(DrEnterpriseService drEnterpriseService) {
        this.drEnterpriseService = drEnterpriseService;
    }

    // Получить все предприятия
    @GetMapping
    public ResponseEntity<List<DrEnterprise>> getAllDrEnterprises() {
        return ResponseEntity.ok(
                drEnterpriseService.getAllDrEnterprises()
        );
    }

    // Получить предприятие по ID
    @GetMapping("/{id}")
    public ResponseEntity<DrEnterprise> getDrEnterpriseById(
            @PathVariable("id") Long id
    ) {
        return drEnterpriseService.getDrEnterpriseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создать предприятие
    @PostMapping
    public ResponseEntity<DrEnterprise> createDrEnterprise(
            @RequestBody DrEnterpriseRequest drEnterprise
    ) {

        DrEnterprise saved =
                drEnterpriseService.createDrEnterprise(drEnterprise);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Обновить предприятие
    @PutMapping("/{id}")
    public ResponseEntity<DrEnterprise> updateDrEnterprise(
            @PathVariable("id") Long id,
            @RequestBody DrEnterpriseRequest drEnterprise
    ) {
        try {
            DrEnterprise updated =
                    drEnterpriseService.updateDrEnterprise(id, drEnterprise);

            return ResponseEntity.ok(updated);
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Удалить предприятие
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrEnterprise(
            @PathVariable("id") Long id
    ) {
        try {
            drEnterpriseService.deleteDrEnterprise(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
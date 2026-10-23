package kg.home.demo.controller;

import kg.home.demo.entity.DrTypeEnterprise;
import kg.home.demo.service.DrTypeEnterpriseService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dr-type-enterprises")
public class DrTypeEnterpriseController {

    private final DrTypeEnterpriseService drTypeEnterpriseService;

    public DrTypeEnterpriseController(
            DrTypeEnterpriseService drTypeEnterpriseService
    ) {
        this.drTypeEnterpriseService = drTypeEnterpriseService;
    }

    // Получить все типы предприятий
    @GetMapping
    public ResponseEntity<List<DrTypeEnterprise>> getAllDrTypeEnterprises() {
        return ResponseEntity.ok(
                drTypeEnterpriseService.getAllDrTypeEnterprises()
        );
    }

    // Получить тип предприятия по ID
    @GetMapping("/{id}")
    public ResponseEntity<DrTypeEnterprise> getDrTypeEnterpriseById(
            @PathVariable("id") Long id
    ) {
        return drTypeEnterpriseService.getDrTypeEnterpriseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создать тип предприятия
    @PostMapping
    public ResponseEntity<DrTypeEnterprise> createDrTypeEnterprise(
            @RequestBody DrTypeEnterprise drTypeEnterprise
    ) {
        drTypeEnterprise.setId(null);

        DrTypeEnterprise saved =
                drTypeEnterpriseService.createDrTypeEnterprise(drTypeEnterprise);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Обновить тип предприятия
    @PutMapping("/{id}")
    public ResponseEntity<DrTypeEnterprise> updateDrTypeEnterprise(
            @PathVariable("id") Long id,
            @RequestBody DrTypeEnterprise drTypeEnterprise
    ) {
        try {
            DrTypeEnterprise updated =
                    drTypeEnterpriseService.updateDrTypeEnterprise(
                            id, drTypeEnterprise
                    );

            return ResponseEntity.ok(updated);
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Удалить тип предприятия
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrTypeEnterprise(
            @PathVariable("id") Long id
    ) {
        try {
            drTypeEnterpriseService.deleteDrTypeEnterprise(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
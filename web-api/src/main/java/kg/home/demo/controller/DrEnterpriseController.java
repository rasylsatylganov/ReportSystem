package kg.home.demo.controller;

import kg.home.demo.controller.api.DrEnterpriseControllerApi;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DrEnterpriseController implements DrEnterpriseControllerApi {


    private final DrEnterpriseService drEnterpriseService;

    @Override
    public ResponseEntity<PageResult<DrEnterprise>> findAllDrEnterprises(
            int page,
            int size, Long typeEnterpriseId) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("sortIndex", "id"));
        return ResponseEntity.ok(typeEnterpriseId == null
                ? drEnterpriseService.findAllDrEnterprises(pageable)
                : drEnterpriseService.findDrEnterprisesByType(typeEnterpriseId, pageable));
    }

    @Override
    public ResponseEntity<DrEnterprise> findDrEnterpriseById(Long id) {
        return ResponseEntity.ok(drEnterpriseService.findDrEnterpriseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Предприятие", id)));
    }

    @Override
    public ResponseEntity<DrEnterprise> createDrEnterprise(DrEnterpriseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drEnterpriseService.createDrEnterprise(request));
    }

    @Override
    public ResponseEntity<DrEnterprise> updateDrEnterprise(
            Long id, DrEnterpriseRequest request) {

        return ResponseEntity.ok(drEnterpriseService.updateDrEnterprise(id, request));
    }

    @Override
    public ResponseEntity<Void> deleteDrEnterprise(Long id) {
        drEnterpriseService.deleteDrEnterprise(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> moveUp(Long id, Long typeEnterpriseId) {
        drEnterpriseService.moveUp(id, typeEnterpriseId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> moveDown(Long id, Long typeEnterpriseId) {
        drEnterpriseService.moveDown(id, typeEnterpriseId);
        return ResponseEntity.noContent().build();
    }
}

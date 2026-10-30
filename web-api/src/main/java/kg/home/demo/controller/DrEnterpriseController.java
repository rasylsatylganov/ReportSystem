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
            int size) {

        return ResponseEntity.ok(
                drEnterpriseService.findAllDrEnterprises(PageRequest.of(page, size, Sort.by("id"))));
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
}

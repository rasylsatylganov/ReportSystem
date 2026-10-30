package kg.home.demo.controller;

import kg.home.demo.controller.api.DrTypeEnterpriseControllerApi;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DrTypeEnterpriseController implements DrTypeEnterpriseControllerApi {


    private final DrTypeEnterpriseService drTypeEnterpriseService;

    @Override
    public ResponseEntity<PageResult<DrTypeEnterprise>> findAllDrTypeEnterprises(
            int page,
            int size) {

        return ResponseEntity.ok(
                drTypeEnterpriseService.findAllDrTypeEnterprises(PageRequest.of(page, size, Sort.by("id"))));
    }

    @Override
    public ResponseEntity<DrTypeEnterprise> findDrTypeEnterpriseById(Long id) {
        return ResponseEntity.ok(drTypeEnterpriseService.findDrTypeEnterpriseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тип предприятия", id)));
    }

    @Override
    public ResponseEntity<DrTypeEnterprise> createDrTypeEnterprise(
            DrTypeEnterpriseRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(drTypeEnterpriseService.createDrTypeEnterprise(request));
    }

    @Override
    public ResponseEntity<DrTypeEnterprise> updateDrTypeEnterprise(
            Long id, DrTypeEnterpriseRequest request) {

        return ResponseEntity.ok(drTypeEnterpriseService.updateDrTypeEnterprise(id, request));
    }

    @Override
    public ResponseEntity<Void> deleteDrTypeEnterprise(Long id) {
        drTypeEnterpriseService.deleteDrTypeEnterprise(id);
        return ResponseEntity.noContent().build();
    }
}

package kg.home.demo.controller;

import kg.home.demo.controller.api.DrReferenceControllerApi;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrReference;
import kg.home.demo.model.DrReferenceRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.service.DrReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DrReferenceController implements DrReferenceControllerApi {


    private final DrReferenceService drReferenceService;

    @Override
    public ResponseEntity<PageResult<DrReference>> findAllDrReferences(
            int page,
            int size) {

        return ResponseEntity.ok(
                drReferenceService.findAllDrReferences(PageRequest.of(page, size, Sort.by("id"))));
    }

    @Override
    public ResponseEntity<DrReference> findDrReferenceById(Long id) {
        return ResponseEntity.ok(drReferenceService.findDrReferenceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Справочник", id)));
    }

    @Override
    public ResponseEntity<DrReference> createDrReference(DrReferenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drReferenceService.createDrReference(request));
    }

    @Override
    public ResponseEntity<DrReference> updateDrReference(
            Long id, DrReferenceRequest request) {

        return ResponseEntity.ok(drReferenceService.updateDrReference(id, request));
    }

    @Override
    public ResponseEntity<Void> deleteDrReference(Long id) {
        drReferenceService.deleteDrReference(id);
        return ResponseEntity.noContent().build();
    }
}

package kg.home.demo.controller;

import kg.home.demo.controller.api.DrReferenceItemControllerApi;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.model.DrReferenceItem;
import kg.home.demo.model.DrReferenceItemRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.service.DrReferenceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DrReferenceItemController implements DrReferenceItemControllerApi {


    private final DrReferenceItemService drReferenceItemService;

    @Override
    public ResponseEntity<PageResult<DrReferenceItem>> findAllDrReferenceItems(
            int page,
            int size) {

        return ResponseEntity.ok(
                drReferenceItemService.findAllDrReferenceItems(PageRequest.of(page, size, Sort.by("id"))));
    }

    @Override
    public ResponseEntity<DrReferenceItem> findDrReferenceItemById(Long id) {
        return ResponseEntity.ok(drReferenceItemService.findDrReferenceItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Элемент справочника", id)));
    }

    @Override
    public ResponseEntity<DrReferenceItem> createDrReferenceItem(DrReferenceItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drReferenceItemService.createDrReferenceItem(request));
    }

    @Override
    public ResponseEntity<DrReferenceItem> updateDrReferenceItem(
            Long id, DrReferenceItemRequest request) {

        return ResponseEntity.ok(drReferenceItemService.updateDrReferenceItem(id, request));
    }

    @Override
    public ResponseEntity<Void> deleteDrReferenceItem(Long id) {
        drReferenceItemService.deleteDrReferenceItem(id);
        return ResponseEntity.noContent().build();
    }
}

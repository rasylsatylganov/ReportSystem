package kg.home.demo.service.impl;

import kg.home.demo.entity.DrReferenceItemEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrReferenceItemMapper;
import kg.home.demo.model.DrReferenceItem;
import kg.home.demo.model.DrReferenceItemRequest;
import kg.home.demo.repository.DrReferenceItemRepository;
import kg.home.demo.service.DrReferenceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrReferenceItemServiceImpl implements DrReferenceItemService {


    private final DrReferenceItemRepository drReferenceItemRepository;
    private final DrReferenceItemMapper mapper;

    @Override
    public List<DrReferenceItem> findAllDrReferenceItems() {
        return drReferenceItemRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<DrReferenceItem> findDrReferenceItemById(Long id) {
        return drReferenceItemRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrReferenceItem createDrReferenceItem(DrReferenceItemRequest request) {
        DrReferenceItemEntity entity = new DrReferenceItemEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(drReferenceItemRepository.save(entity));
    }

    @Override
    @Transactional
    public DrReferenceItem updateDrReferenceItem(Long id, DrReferenceItemRequest request) {
        DrReferenceItemEntity entity = drReferenceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Элемент справочника", id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(drReferenceItemRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDrReferenceItem(Long id) {
        if (!drReferenceItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Элемент справочника", id);
        }
        drReferenceItemRepository.deleteById(id);
    }

}

package kg.home.demo.service.impl;

import kg.home.demo.entity.DrReferenceItemEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrReferenceItemMapper;
import kg.home.demo.model.DrReferenceItem;
import kg.home.demo.model.DrReferenceItemRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.repository.DrReferenceItemRepository;
import kg.home.demo.service.DrReferenceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrReferenceItemServiceImpl implements DrReferenceItemService {


    private final DrReferenceItemRepository drReferenceItemRepository;
    private final DrReferenceItemMapper mapper;

    @Override
    public PageResult<DrReferenceItem> findAllDrReferenceItems(Pageable pageable) {
        return PageResult.from(drReferenceItemRepository.findAll(pageable).map(mapper::toModel));
    }

    @Override
    public PageResult<DrReferenceItem> findDrReferenceItemsByReference(Long referenceId, Pageable pageable) {
        return PageResult.from(drReferenceItemRepository.findByReference_Id(referenceId, pageable)
                .map(mapper::toModel));
    }

    @Override
    public Optional<DrReferenceItem> findDrReferenceItemById(Long id) {
        return drReferenceItemRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrReferenceItem createDrReferenceItem(DrReferenceItemRequest request) {
        drReferenceItemRepository.findAllForUpdate();
        DrReferenceItemEntity entity = new DrReferenceItemEntity();
        mapper.applyRequest(entity, request);
        entity.setSortIndex(drReferenceItemRepository.nextSortIndex());
        return mapper.toModel(drReferenceItemRepository.save(entity));
    }

    @Override
    @Transactional
    public DrReferenceItem updateDrReferenceItem(Long id, DrReferenceItemRequest request) {
        DrReferenceItemEntity entity = findRequired(drReferenceItemRepository.findAllForUpdate(), id);
        mapper.applyRequest(entity, request);
        return mapper.toModel(drReferenceItemRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDrReferenceItem(Long id) {
        drReferenceItemRepository.delete(findRequired(drReferenceItemRepository.findAllForUpdate(), id));
    }

    @Override
    @Transactional
    public void moveUp(Long id, Long referenceId) {
        move(id, referenceId, -1);
    }

    @Override
    @Transactional
    public void moveDown(Long id, Long referenceId) {
        move(id, referenceId, 1);
    }

    private void move(Long id, Long referenceId, int offset) {
        List<DrReferenceItemEntity> locked = drReferenceItemRepository.findAllForUpdate();
        DrReferenceItemEntity current = findRequired(locked, id);
        if (referenceId != null && !current.getReference().getId().equals(referenceId)) {
            throw new ResourceNotFoundException("Значение выбранного справочника", id);
        }
        List<DrReferenceItemEntity> ordered = locked.stream()
                .filter(item -> referenceId == null ||
                        item.getReference().getId().equals(referenceId))
                .sorted(Comparator.comparing(DrReferenceItemEntity::getSortIndex).thenComparing(DrReferenceItemEntity::getId))
                .toList();
        int neighborIndex = ordered.indexOf(current) + offset;
        if (neighborIndex < 0 || neighborIndex >= ordered.size()) return;
        DrReferenceItemEntity neighbor = ordered.get(neighborIndex);
        Long previousIndex = current.getSortIndex();
        current.setSortIndex(neighbor.getSortIndex());
        neighbor.setSortIndex(previousIndex);
        drReferenceItemRepository.saveAll(List.of(current, neighbor));
        drReferenceItemRepository.flush();
    }

    private DrReferenceItemEntity findRequired(List<DrReferenceItemEntity> items, Long id) {
        return items.stream().filter(item -> item.getId().equals(id)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Значение справочника", id));
    }

}

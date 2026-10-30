package kg.home.demo.service.impl;

import kg.home.demo.entity.DrReferenceEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrReferenceMapper;
import kg.home.demo.model.DrReference;
import kg.home.demo.model.DrReferenceRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.repository.DrReferenceRepository;
import kg.home.demo.service.DrReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrReferenceServiceImpl implements DrReferenceService {

    private final DrReferenceRepository repository;
    private final DrReferenceMapper mapper;

    @Override
    public PageResult<DrReference> findAllDrReferences(Pageable pageable) {
        return PageResult.from(repository.findAll(pageable).map(mapper::toModel));
    }

    @Override
    public Optional<DrReference> findDrReferenceById(Long id) {
        return repository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrReference createDrReference(DrReferenceRequest request) {
        repository.findAllForUpdate();
        DrReferenceEntity entity = new DrReferenceEntity();
        mapper.applyRequest(entity, request);
        entity.setSortIndex(repository.nextSortIndex());
        return mapper.toModel(repository.save(entity));
    }

    @Override
    @Transactional
    public DrReference updateDrReference(Long id, DrReferenceRequest request) {
        DrReferenceEntity existing = findRequired(repository.findAllForUpdate(), id);
        mapper.applyRequest(existing, request);
        // Обычный PUT не меняет порядок записи.
        return mapper.toModel(repository.save(existing));
    }

    @Override
    @Transactional
    public void deleteDrReference(Long id) {
        DrReferenceEntity existing = findRequired(repository.findAllForUpdate(), id);
        repository.delete(existing);
    }

    @Override
    @Transactional
    public void moveUp(Long id) {
        move(id, -1);
    }

    @Override
    @Transactional
    public void moveDown(Long id) {
        move(id, 1);
    }

    private void move(Long id, int offset) {
        List<DrReferenceEntity> references = new ArrayList<>(repository.findAllForUpdate());
        DrReferenceEntity current = findRequired(references, id);
        references.sort(Comparator.comparing(DrReferenceEntity::getSortIndex)
                .thenComparing(DrReferenceEntity::getId));

        int currentIndex = references.indexOf(current);
        int neighborIndex = currentIndex + offset;
        // Граница списка: операция ничего не меняет.
        if (neighborIndex < 0 || neighborIndex >= references.size()) {
            return;
        }

        DrReferenceEntity neighbor = references.get(neighborIndex);
        Long currentSortIndex = current.getSortIndex();
        current.setSortIndex(neighbor.getSortIndex());
        neighbor.setSortIndex(currentSortIndex);

        repository.saveAll(List.of(current, neighbor));
        repository.flush();
    }

    private DrReferenceEntity findRequired(List<DrReferenceEntity> references, Long id) {
        return references.stream()
                .filter(reference -> reference.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Справочник", id));
    }

}

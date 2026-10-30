package kg.home.demo.service.impl;

import kg.home.demo.entity.DrTypeEnterpriseEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrTypeEnterpriseMapper;
import kg.home.demo.model.DrTypeEnterprise;
import kg.home.demo.model.DrTypeEnterpriseRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.repository.DrTypeEnterpriseRepository;
import kg.home.demo.service.DrTypeEnterpriseService;
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
public class DrTypeEnterpriseServiceImpl implements DrTypeEnterpriseService {

    private final DrTypeEnterpriseRepository repository;
    private final DrTypeEnterpriseMapper mapper;

    @Override
    public PageResult<DrTypeEnterprise> findAllDrTypeEnterprises(Pageable pageable) {
        return PageResult.from(repository.findAll(pageable).map(mapper::toModel));
    }

    @Override
    public Optional<DrTypeEnterprise> findDrTypeEnterpriseById(Long id) {
        return repository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrTypeEnterprise createDrTypeEnterprise(DrTypeEnterpriseRequest request) {
        repository.findAllForUpdate();
        DrTypeEnterpriseEntity entity = new DrTypeEnterpriseEntity();
        mapper.applyRequest(entity, request);
        entity.setSortIndex(repository.nextSortIndex());
        return mapper.toModel(repository.save(entity));
    }

    @Override
    @Transactional
    public DrTypeEnterprise updateDrTypeEnterprise(Long id, DrTypeEnterpriseRequest request) {
        DrTypeEnterpriseEntity existing = findRequired(repository.findAllForUpdate(), id);
        mapper.applyRequest(existing, request);
        // Обычный PUT не меняет порядок записи.
        return mapper.toModel(repository.save(existing));
    }

    @Override
    @Transactional
    public void deleteDrTypeEnterprise(Long id) {
        DrTypeEnterpriseEntity existing = findRequired(repository.findAllForUpdate(), id);
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
        List<DrTypeEnterpriseEntity> enterprises = new ArrayList<>(repository.findAllForUpdate());
        DrTypeEnterpriseEntity current = findRequired(enterprises, id);
        enterprises.sort(Comparator.comparing(DrTypeEnterpriseEntity::getSortIndex)
                .thenComparing(DrTypeEnterpriseEntity::getId));

        int currentIndex = enterprises.indexOf(current);
        int neighborIndex = currentIndex + offset;
        // Граница списка: операция ничего не меняет.
        if (neighborIndex < 0 || neighborIndex >= enterprises.size()) {
            return;
        }

        DrTypeEnterpriseEntity neighbor = enterprises.get(neighborIndex);
        Long currentSortIndex = current.getSortIndex();
        current.setSortIndex(neighbor.getSortIndex());
        neighbor.setSortIndex(currentSortIndex);

        repository.saveAll(List.of(current, neighbor));
        repository.flush();
    }

    private DrTypeEnterpriseEntity findRequired(List<DrTypeEnterpriseEntity> enterprises, Long id) {
        return enterprises.stream()
                .filter(period -> period.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Тип предприятия", id));
    }

}

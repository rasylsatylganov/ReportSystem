package kg.home.demo.service.impl;

import kg.home.demo.entity.DrPeriodEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrPeriodMapper;
import kg.home.demo.model.DrPeriod;
import kg.home.demo.model.DrPeriodRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.repository.DrPeriodRepository;
import kg.home.demo.service.DrPeriodService;
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
public class DrPeriodServiceImpl implements DrPeriodService {


    private final DrPeriodRepository drPeriodRepository;
    private final DrPeriodMapper mapper;

    @Override
    public PageResult<DrPeriod> findAllDrPeriods(Pageable pageable) {
        return PageResult.from(drPeriodRepository.findAll(pageable).map(mapper::toModel));
    }

    @Override
    public PageResult<DrPeriod> findDrPeriodsByType(Long typePeriodId, Pageable pageable) {
        return PageResult.from(drPeriodRepository.findByTypePeriod_Id(typePeriodId, pageable).map(mapper::toModel));
    }

    @Override
    public Optional<DrPeriod> findDrPeriodById(Long id) {
        return drPeriodRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrPeriod createDrPeriod(DrPeriodRequest request) {
        drPeriodRepository.findAllForUpdate();
        DrPeriodEntity entity = new DrPeriodEntity();
        mapper.applyRequest(entity, request);
        entity.setSortIndex(drPeriodRepository.nextSortIndex());
        return mapper.toModel(drPeriodRepository.save(entity));
    }

    @Override
    @Transactional
    public DrPeriod updateDrPeriod(Long id, DrPeriodRequest request) {
        DrPeriodEntity entity = findRequired(drPeriodRepository.findAllForUpdate(), id);
        mapper.applyRequest(entity, request);
        return mapper.toModel(drPeriodRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDrPeriod(Long id) {
        drPeriodRepository.delete(findRequired(drPeriodRepository.findAllForUpdate(), id));
    }

    @Override
    @Transactional
    public void moveUp(Long id, Long typePeriodId) {
        move(id, typePeriodId, -1);
    }

    @Override
    @Transactional
    public void moveDown(Long id, Long typePeriodId) {
        move(id, typePeriodId, 1);
    }

    private void move(Long id, Long typePeriodId, int offset) {
        List<DrPeriodEntity> locked = drPeriodRepository.findAllForUpdate();
        DrPeriodEntity current = findRequired(locked, id);
        if (typePeriodId != null && !current.getTypePeriod().getId().equals(typePeriodId)) {
            throw new ResourceNotFoundException("Период выбранного типа", id);
        }
        List<DrPeriodEntity> ordered = locked.stream()
                .filter(period -> typePeriodId == null || period.getTypePeriod().getId().equals(typePeriodId))
                .sorted(Comparator.comparing(DrPeriodEntity::getSortIndex).thenComparing(DrPeriodEntity::getId))
                .toList();
        int neighborIndex = ordered.indexOf(current) + offset;
        if (neighborIndex < 0 || neighborIndex >= ordered.size()) return;
        DrPeriodEntity neighbor = ordered.get(neighborIndex);
        Long previousIndex = current.getSortIndex();
        current.setSortIndex(neighbor.getSortIndex());
        neighbor.setSortIndex(previousIndex);
        drPeriodRepository.saveAll(List.of(current, neighbor));
        drPeriodRepository.flush();
    }

    private DrPeriodEntity findRequired(List<DrPeriodEntity> periods, Long id) {
        return periods.stream().filter(period -> period.getId().equals(id)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Период", id));
    }

}

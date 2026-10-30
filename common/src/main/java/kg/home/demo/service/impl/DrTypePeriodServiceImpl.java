package kg.home.demo.service.impl;

import jakarta.persistence.EntityNotFoundException;
import kg.home.demo.entity.DrTypePeriodEntity;
import kg.home.demo.mapper.DrTypePeriodMapper;
import kg.home.demo.model.DrTypePeriod;
import kg.home.demo.model.DrTypePeriodRequest;
import kg.home.demo.repository.DrTypePeriodRepository;
import kg.home.demo.service.DrTypePeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrTypePeriodServiceImpl implements DrTypePeriodService {

    private final DrTypePeriodRepository repository;
    private final DrTypePeriodMapper mapper;

    @Override
    public List<DrTypePeriod> getAllDrTypePeriods() {
        return repository.findAllByOrderBySortIndexAscIdAsc().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<DrTypePeriod> getDrTypePeriodById(Long id) {
        return repository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrTypePeriod createDrTypePeriod(DrTypePeriodRequest request) {
        validate(request);
        repository.findAllForUpdate();
        DrTypePeriodEntity entity = new DrTypePeriodEntity();
        mapper.applyRequest(entity, request);
        entity.setSortIndex(repository.nextSortIndex());
        return mapper.toModel(repository.save(entity));
    }

    @Override
    @Transactional
    public DrTypePeriod updateDrTypePeriod(Long id, DrTypePeriodRequest request) {
        validate(request);
        DrTypePeriodEntity existing = findRequired(repository.findAllForUpdate(), id);
        mapper.applyRequest(existing, request);
        // Обычный PUT не меняет порядок записи.
        return mapper.toModel(repository.save(existing));
    }

    @Override
    @Transactional
    public void deleteDrTypePeriod(Long id) {
        DrTypePeriodEntity existing = findRequired(repository.findAllForUpdate(), id);
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
        List<DrTypePeriodEntity> periods = new ArrayList<>(repository.findAllForUpdate());
        DrTypePeriodEntity current = findRequired(periods, id);
        periods.sort(Comparator.comparing(DrTypePeriodEntity::getSortIndex)
                .thenComparing(DrTypePeriodEntity::getId));

        int currentIndex = periods.indexOf(current);
        int neighborIndex = currentIndex + offset;
        // Граница списка: операция ничего не меняет.
        if (neighborIndex < 0 || neighborIndex >= periods.size()) {
            return;
        }

        DrTypePeriodEntity neighbor = periods.get(neighborIndex);
        Long currentSortIndex = current.getSortIndex();
        current.setSortIndex(neighbor.getSortIndex());
        neighbor.setSortIndex(currentSortIndex);

        // Обе Entity управляются JPA. Dirty checking сохранит ОБА изменения
        // при завершении одной транзакции. При ошибке она откатится целиком.
        repository.flush();
    }

    private DrTypePeriodEntity findRequired(List<DrTypePeriodEntity> periods, Long id) {
        return periods.stream()
                .filter(period -> period.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Тип периода не найден: " + id));
    }

    private void validate(DrTypePeriodRequest request) {
        if (request.codeTypePeriod() == null || request.codeTypePeriod().isBlank() ||
                request.codeTypePeriod().length() > 3) {
            throw new IllegalArgumentException("Код обязателен и должен содержать не более 3 символов.");
        }
        if (request.nameTypePeriod() == null || request.nameTypePeriod().isBlank() ||
                request.nameTypePeriod().length() > 100) {
            throw new IllegalArgumentException("Наименование обязательно и должно содержать не более 100 символов.");
        }
    }
}

package kg.home.demo.service.impl;

import jakarta.persistence.EntityNotFoundException;
import kg.home.demo.entity.DrTypePeriodEntity;
import kg.home.demo.repository.DrTypePeriodRepository;
import kg.home.demo.service.DrTypePeriodService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DrTypePeriodServiceImpl implements DrTypePeriodService {

    private final DrTypePeriodRepository repository;

    public DrTypePeriodServiceImpl(DrTypePeriodRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DrTypePeriodEntity> getAllDrTypePeriods() {
        return repository.findAllByOrderBySortIndexAscIdAsc();
    }

    @Override
    public Optional<DrTypePeriodEntity> getDrTypePeriodById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public DrTypePeriodEntity createDrTypePeriod(DrTypePeriodEntity drTypePeriod) {
        validate(drTypePeriod);
        repository.findAllForUpdate();
        drTypePeriod.setId(null);
        drTypePeriod.setSortIndex(repository.nextSortIndex());
        return repository.save(drTypePeriod);
    }

    @Override
    @Transactional
    public DrTypePeriodEntity updateDrTypePeriod(Long id, DrTypePeriodEntity drTypePeriod) {
        validate(drTypePeriod);
        DrTypePeriodEntity existing = findRequired(repository.findAllForUpdate(), id);
        existing.setCodeTypePeriod(drTypePeriod.getCodeTypePeriod());
        existing.setNameTypePeriod(drTypePeriod.getNameTypePeriod());
        // Обычный PUT не меняет порядок записи.
        return repository.save(existing);
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

    private void validate(DrTypePeriodEntity period) {
        if (period.getCodeTypePeriod() == null || period.getCodeTypePeriod().isBlank()
                || period.getCodeTypePeriod().length() > 3) {
            throw new IllegalArgumentException("Код обязателен и должен содержать не более 3 символов.");
        }
        if (period.getNameTypePeriod() == null || period.getNameTypePeriod().isBlank()
                || period.getNameTypePeriod().length() > 100) {
            throw new IllegalArgumentException("Наименование обязательно и должно содержать не более 100 символов.");
        }
    }
}

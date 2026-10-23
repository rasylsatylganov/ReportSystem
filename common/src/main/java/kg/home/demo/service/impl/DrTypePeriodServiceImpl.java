package kg.home.demo.service.impl;

import kg.home.demo.entity.DrTypePeriod;
import kg.home.demo.repository.DrTypePeriodRepository;
import kg.home.demo.service.DrTypePeriodService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DrTypePeriodServiceImpl implements DrTypePeriodService {

    private final DrTypePeriodRepository drTypePeriodRepository;

    public DrTypePeriodServiceImpl(DrTypePeriodRepository drTypePeriodRepository) {
        this.drTypePeriodRepository = drTypePeriodRepository;
    }

    @Override
    public List<DrTypePeriod> getAllDrTypePeriods() {
        return drTypePeriodRepository.findAll();
    }

    @Override
    public Optional<DrTypePeriod> getDrTypePeriodById(Long id) {
        return drTypePeriodRepository.findById(id);
    }

    @Override
    public DrTypePeriod createDrTypePeriod(DrTypePeriod drTypePeriod) {
        return drTypePeriodRepository.save(drTypePeriod);
    }

    @Override
    public DrTypePeriod updateDrTypePeriod(Long id, DrTypePeriod drTypePeriod) {
        return drTypePeriodRepository.findById(id)
                .map(existing -> {
                    existing.setCodeTypePeriod(drTypePeriod.getCodeTypePeriod());
                    existing.setNameTypePeriod(drTypePeriod.getNameTypePeriod());
                    return drTypePeriodRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DrTypePeriod not found with id " + id));
    }

    @Override
    public void deleteDrTypePeriod(Long id) {
        if (!drTypePeriodRepository.existsById(id)) {
            throw new RuntimeException("DrTypePeriod not found with id " + id);
        }
        drTypePeriodRepository.deleteById(id);
    }
}

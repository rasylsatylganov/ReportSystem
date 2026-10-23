package kg.home.demo.service.impl;

import kg.home.demo.entity.DrPeriod;
import kg.home.demo.repository.DrPeriodRepository;
import kg.home.demo.service.DrPeriodService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DrPeriodServiceImpl implements DrPeriodService {

    private final DrPeriodRepository drPeriodRepository;

    public DrPeriodServiceImpl(DrPeriodRepository drPeriodRepository) {
        this.drPeriodRepository = drPeriodRepository;
    }

    @Override
    public List<DrPeriod> getAllDrPeriods() {
        return drPeriodRepository.findAll();
    }

    @Override
    public Optional<DrPeriod> getDrPeriodById(Long id) {
        return drPeriodRepository.findById(id);
    }

    @Override
    public DrPeriod createDrPeriod(DrPeriod drPeriod) {
        return drPeriodRepository.save(drPeriod);
    }

    @Override
    public DrPeriod updateDrPeriod(Long id, DrPeriod drPeriod) {
        return drPeriodRepository.findById(id)
                .map(existing -> {
                    existing.setTypePeriod(drPeriod.getTypePeriod());
                    existing.setCodePeriod(drPeriod.getCodePeriod());
                    existing.setNamePeriod(drPeriod.getNamePeriod());
                    existing.setDateBegin(drPeriod.getDateBegin());
                    existing.setDateEnd(drPeriod.getDateEnd());
                    return drPeriodRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DrPeriod not found with id " + id));
    }

    @Override
    public void deleteDrPeriod(Long id) {
        if (!drPeriodRepository.existsById(id)) {
            throw new RuntimeException("DrPeriod not found with id " + id);
        }
        drPeriodRepository.deleteById(id);
    }
}

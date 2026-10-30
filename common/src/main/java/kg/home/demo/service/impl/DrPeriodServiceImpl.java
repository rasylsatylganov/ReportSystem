package kg.home.demo.service.impl;

import kg.home.demo.entity.DrPeriodEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrPeriodMapper;
import kg.home.demo.model.DrPeriod;
import kg.home.demo.model.DrPeriodRequest;
import kg.home.demo.repository.DrPeriodRepository;
import kg.home.demo.service.DrPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrPeriodServiceImpl implements DrPeriodService {


    private final DrPeriodRepository drPeriodRepository;
    private final DrPeriodMapper mapper;

    @Override
    public List<DrPeriod> getAllDrPeriods() {
        return drPeriodRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<DrPeriod> getDrPeriodById(Long id) {
        return drPeriodRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrPeriod createDrPeriod(DrPeriodRequest request) {
        DrPeriodEntity entity = new DrPeriodEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(drPeriodRepository.save(entity));
    }

    @Override
    @Transactional
    public DrPeriod updateDrPeriod(Long id, DrPeriodRequest request) {
        DrPeriodEntity entity = drPeriodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Период", id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(drPeriodRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDrPeriod(Long id) {
        if (!drPeriodRepository.existsById(id)) {
            throw new ResourceNotFoundException("Период", id);
        }
        drPeriodRepository.deleteById(id);
    }

}

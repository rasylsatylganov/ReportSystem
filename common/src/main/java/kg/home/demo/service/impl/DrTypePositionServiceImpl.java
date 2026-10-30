package kg.home.demo.service.impl;

import kg.home.demo.entity.DrTypePositionEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrTypePositionMapper;
import kg.home.demo.model.DrTypePosition;
import kg.home.demo.model.DrTypePositionRequest;
import kg.home.demo.repository.DrTypePositionRepository;
import kg.home.demo.service.DrTypePositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrTypePositionServiceImpl implements DrTypePositionService {


    private final DrTypePositionRepository drTypePositionRepository;
    private final DrTypePositionMapper mapper;

    @Override
    public List<DrTypePosition> findAllDrTypePositions() {
        return drTypePositionRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<DrTypePosition> findDrTypePositionById(Long id) {
        return drTypePositionRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrTypePosition createDrTypePosition(DrTypePositionRequest request) {
        DrTypePositionEntity entity = new DrTypePositionEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(drTypePositionRepository.save(entity));
    }

    @Override
    @Transactional
    public DrTypePosition updateDrTypePosition(Long id, DrTypePositionRequest request) {
        DrTypePositionEntity entity = drTypePositionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тип расположения", id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(drTypePositionRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDrTypePosition(Long id) {
        if (!drTypePositionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Тип расположения", id);
        }
        drTypePositionRepository.deleteById(id);
    }

}

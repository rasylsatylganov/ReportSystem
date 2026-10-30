package kg.home.demo.service.impl;

import kg.home.demo.entity.DrTypePositionEntity;
import kg.home.demo.repository.DrTypePositionRepository;
import kg.home.demo.service.DrTypePositionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DrTypePositionServiceImpl implements DrTypePositionService {

    private final DrTypePositionRepository drTypePositionRepository;

    public DrTypePositionServiceImpl(DrTypePositionRepository drTypePositionRepository) {
        this.drTypePositionRepository = drTypePositionRepository;
    }

    @Override
    public List<DrTypePositionEntity> getAllDrTypePositions() {
        return drTypePositionRepository.findAll();
    }

    @Override
    public Optional<DrTypePositionEntity> getDrTypePositionById(Long id) {
        return drTypePositionRepository.findById(id);
    }

    @Override
    public DrTypePositionEntity createDrTypePosition(DrTypePositionEntity drTypePosition) {
        return drTypePositionRepository.save(drTypePosition);
    }

    @Override
    public DrTypePositionEntity updateDrTypePosition(Long id, DrTypePositionEntity drTypePosition) {
        return drTypePositionRepository.findById(id)
                .map(existing -> {
                    existing.setNameTypePosition(drTypePosition.getNameTypePosition());
                    return drTypePositionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DrTypePositionEntity not found with id " + id));
    }

    @Override
    public void deleteDrTypePosition(Long id) {
        if (!drTypePositionRepository.existsById(id)) {
            throw new RuntimeException("DrTypePositionEntity not found with id " + id);
        }
        drTypePositionRepository.deleteById(id);
    }
}

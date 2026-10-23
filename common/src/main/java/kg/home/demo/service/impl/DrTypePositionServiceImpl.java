package kg.home.demo.service.impl;

import kg.home.demo.entity.DrTypePosition;
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
    public List<DrTypePosition> getAllDrTypePositions() {
        return drTypePositionRepository.findAll();
    }

    @Override
    public Optional<DrTypePosition> getDrTypePositionById(Long id) {
        return drTypePositionRepository.findById(id);
    }

    @Override
    public DrTypePosition createDrTypePosition(DrTypePosition drTypePosition) {
        return drTypePositionRepository.save(drTypePosition);
    }

    @Override
    public DrTypePosition updateDrTypePosition(Long id, DrTypePosition drTypePosition) {
        return drTypePositionRepository.findById(id)
                .map(existing -> {
                    existing.setNameTypePosition(drTypePosition.getNameTypePosition());
                    return drTypePositionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DrTypePosition not found with id " + id));
    }

    @Override
    public void deleteDrTypePosition(Long id) {
        if (!drTypePositionRepository.existsById(id)) {
            throw new RuntimeException("DrTypePosition not found with id " + id);
        }
        drTypePositionRepository.deleteById(id);
    }
}

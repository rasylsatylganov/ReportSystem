package kg.home.demo.service.impl;

import kg.home.demo.entity.DrTypeFieldEntity;
import kg.home.demo.repository.DrTypeFieldRepository;
import kg.home.demo.service.DrTypeFieldService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DrTypeFieldServiceImpl implements DrTypeFieldService {

    private final DrTypeFieldRepository drTypeFieldRepository;

    public DrTypeFieldServiceImpl(DrTypeFieldRepository drTypeFieldRepository) {
        this.drTypeFieldRepository = drTypeFieldRepository;
    }

    @Override
    public List<DrTypeFieldEntity> getAllDrTypeFields() {
        return drTypeFieldRepository.findAll();
    }

    @Override
    public Optional<DrTypeFieldEntity> getDrTypeFieldById(Long id) {
        return drTypeFieldRepository.findById(id);
    }

    @Override
    public DrTypeFieldEntity createDrTypeField(DrTypeFieldEntity drTypeField) {
        return drTypeFieldRepository.save(drTypeField);
    }

    @Override
    public DrTypeFieldEntity updateDrTypeField(Long id, DrTypeFieldEntity drTypeField) {
        return drTypeFieldRepository.findById(id)
                .map(existing -> {
                    existing.setCodeTypeField(drTypeField.getCodeTypeField());
                    existing.setNameTypeField(drTypeField.getNameTypeField());
                    return drTypeFieldRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DrTypeFieldEntity not found with id " + id));
    }

    @Override
    public void deleteDrTypeField(Long id) {
        if (!drTypeFieldRepository.existsById(id)) {
            throw new RuntimeException("DrTypeFieldEntity not found with id " + id);
        }
        drTypeFieldRepository.deleteById(id);
    }
}

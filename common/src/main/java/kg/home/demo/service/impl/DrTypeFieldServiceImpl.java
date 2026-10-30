package kg.home.demo.service.impl;

import kg.home.demo.entity.DrTypeFieldEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrTypeFieldMapper;
import kg.home.demo.model.DrTypeField;
import kg.home.demo.model.DrTypeFieldRequest;
import kg.home.demo.repository.DrTypeFieldRepository;
import kg.home.demo.service.DrTypeFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrTypeFieldServiceImpl implements DrTypeFieldService {


    private final DrTypeFieldRepository drTypeFieldRepository;
    private final DrTypeFieldMapper mapper;

    @Override
    public List<DrTypeField> getAllDrTypeFields() {
        return drTypeFieldRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<DrTypeField> getDrTypeFieldById(Long id) {
        return drTypeFieldRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrTypeField createDrTypeField(DrTypeFieldRequest request) {
        DrTypeFieldEntity entity = new DrTypeFieldEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(drTypeFieldRepository.save(entity));
    }

    @Override
    @Transactional
    public DrTypeField updateDrTypeField(Long id, DrTypeFieldRequest request) {
        DrTypeFieldEntity entity = drTypeFieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тип поля", id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(drTypeFieldRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDrTypeField(Long id) {
        if (!drTypeFieldRepository.existsById(id)) {
            throw new ResourceNotFoundException("Тип поля", id);
        }
        drTypeFieldRepository.deleteById(id);
    }

}

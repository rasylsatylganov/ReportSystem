package kg.home.demo.service.impl;

import kg.home.demo.entity.DrReferenceEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrReferenceMapper;
import kg.home.demo.model.DrReference;
import kg.home.demo.model.DrReferenceRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.repository.DrReferenceRepository;
import kg.home.demo.service.DrReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrReferenceServiceImpl implements DrReferenceService {


    private final DrReferenceRepository drReferenceRepository;
    private final DrReferenceMapper mapper;

    @Override
    public PageResult<DrReference> findAllDrReferences(Pageable pageable) {
        return PageResult.from(drReferenceRepository.findAll(pageable).map(mapper::toModel));
    }

    @Override
    public Optional<DrReference> findDrReferenceById(Long id) {
        return drReferenceRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrReference createDrReference(DrReferenceRequest request) {
        DrReferenceEntity entity = new DrReferenceEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(drReferenceRepository.save(entity));
    }

    @Override
    @Transactional
    public DrReference updateDrReference(Long id, DrReferenceRequest request) {
        DrReferenceEntity entity = drReferenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Справочник", id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(drReferenceRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDrReference(Long id) {
        if (!drReferenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Справочник", id);
        }
        drReferenceRepository.deleteById(id);
    }

}

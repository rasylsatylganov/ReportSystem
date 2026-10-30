package kg.home.demo.service.impl;

import kg.home.demo.entity.DrReferenceEntity;
import kg.home.demo.repository.DrReferenceRepository;
import kg.home.demo.service.DrReferenceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DrReferenceServiceImpl implements DrReferenceService {

    private final DrReferenceRepository drReferenceRepository;

    public DrReferenceServiceImpl(DrReferenceRepository drReferenceRepository) {
        this.drReferenceRepository = drReferenceRepository;
    }

    @Override
    public List<DrReferenceEntity> getAllDrReferences() {
        return drReferenceRepository.findAll();
    }

    @Override
    public Optional<DrReferenceEntity> getDrReferenceById(Long id) {
        return drReferenceRepository.findById(id);
    }

    @Override
    public DrReferenceEntity createDrReference(DrReferenceEntity drReference) {
        return drReferenceRepository.save(drReference);
    }

    @Override
    public DrReferenceEntity updateDrReference(Long id, DrReferenceEntity drReference) {
        return drReferenceRepository.findById(id)
                .map(existing -> {
                    existing.setCodeReference(drReference.getCodeReference());
                    existing.setNameReference(drReference.getNameReference());
                    return drReferenceRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DrReferenceEntity not found with id " + id));
    }

    @Override
    public void deleteDrReference(Long id) {
        if (!drReferenceRepository.existsById(id)) {
            throw new RuntimeException("DrReferenceEntity not found with id " + id);
        }
        drReferenceRepository.deleteById(id);
    }
}

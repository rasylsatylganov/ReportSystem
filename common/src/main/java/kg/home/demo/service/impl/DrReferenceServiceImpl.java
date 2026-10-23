package kg.home.demo.service.impl;

import kg.home.demo.entity.DrReference;
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
    public List<DrReference> getAllDrReferences() {
        return drReferenceRepository.findAll();
    }

    @Override
    public Optional<DrReference> getDrReferenceById(Long id) {
        return drReferenceRepository.findById(id);
    }

    @Override
    public DrReference createDrReference(DrReference drReference) {
        return drReferenceRepository.save(drReference);
    }

    @Override
    public DrReference updateDrReference(Long id, DrReference drReference) {
        return drReferenceRepository.findById(id)
                .map(existing -> {
                    existing.setCodeReference(drReference.getCodeReference());
                    existing.setNameReference(drReference.getNameReference());
                    return drReferenceRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DrReference not found with id " + id));
    }

    @Override
    public void deleteDrReference(Long id) {
        if (!drReferenceRepository.existsById(id)) {
            throw new RuntimeException("DrReference not found with id " + id);
        }
        drReferenceRepository.deleteById(id);
    }
}

package kg.home.demo.service.impl;

import kg.home.demo.entity.DrEnterpriseEntity;
import kg.home.demo.repository.DrEnterpriseRepository;
import kg.home.demo.service.DrEnterpriseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DrEnterpriseServiceImpl implements DrEnterpriseService {

    private final DrEnterpriseRepository drEnterpriseRepository;

    public DrEnterpriseServiceImpl(DrEnterpriseRepository drEnterpriseRepository) {
        this.drEnterpriseRepository = drEnterpriseRepository;
    }

    @Override
    public List<DrEnterpriseEntity> getAllDrEnterprises() {
        return drEnterpriseRepository.findAll();
    }

    @Override
    public Optional<DrEnterpriseEntity> getDrEnterpriseById(Long id) {
        return drEnterpriseRepository.findById(id);
    }

    @Override
    public DrEnterpriseEntity createDrEnterprise(DrEnterpriseEntity drEnterprise) {
        return drEnterpriseRepository.save(drEnterprise);
    }

    @Override
    public DrEnterpriseEntity updateDrEnterprise(Long id, DrEnterpriseEntity drEnterprise) {
        return drEnterpriseRepository.findById(id)
                .map(existing -> {
                    existing.setTypeEnterprise(drEnterprise.getTypeEnterprise());
                    existing.setCodeEnterprise(drEnterprise.getCodeEnterprise());
                    existing.setNameEnterprise(drEnterprise.getNameEnterprise());
                    return drEnterpriseRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DrEnterpriseEntity not found with id " + id));
    }

    @Override
    public void deleteDrEnterprise(Long id) {
        if (!drEnterpriseRepository.existsById(id)) {
            throw new RuntimeException("DrEnterpriseEntity not found with id " + id);
        }
        drEnterpriseRepository.deleteById(id);
    }
}

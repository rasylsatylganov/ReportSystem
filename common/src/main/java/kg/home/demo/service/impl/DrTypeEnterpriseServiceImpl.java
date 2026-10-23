package kg.home.demo.service.impl;

import kg.home.demo.entity.DrTypeEnterprise;
import kg.home.demo.repository.DrTypeEnterpriseRepository;
import kg.home.demo.service.DrTypeEnterpriseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DrTypeEnterpriseServiceImpl implements DrTypeEnterpriseService {

    private final DrTypeEnterpriseRepository drTypeEnterpriseRepository;

    public DrTypeEnterpriseServiceImpl(DrTypeEnterpriseRepository drTypeEnterpriseRepository) {
        this.drTypeEnterpriseRepository = drTypeEnterpriseRepository;
    }

    @Override
    public List<DrTypeEnterprise> getAllDrTypeEnterprises() {
        return drTypeEnterpriseRepository.findAll();
    }

    @Override
    public Optional<DrTypeEnterprise> getDrTypeEnterpriseById(Long id) {
        return drTypeEnterpriseRepository.findById(id);
    }

    @Override
    public DrTypeEnterprise createDrTypeEnterprise(DrTypeEnterprise drTypeEnterprise) {
        return drTypeEnterpriseRepository.save(drTypeEnterprise);
    }

    @Override
    public DrTypeEnterprise updateDrTypeEnterprise(Long id, DrTypeEnterprise drTypeEnterprise) {
        return drTypeEnterpriseRepository.findById(id)
                .map(existing -> {
                    existing.setCodeTypeEnterprise(drTypeEnterprise.getCodeTypeEnterprise());
                    existing.setNameTypeEnterprise(drTypeEnterprise.getNameTypeEnterprise());
                    return drTypeEnterpriseRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DrTypeEnterprise not found with id " + id));
    }

    @Override
    public void deleteDrTypeEnterprise(Long id) {
        if (!drTypeEnterpriseRepository.existsById(id)) {
            throw new RuntimeException("DrTypeEnterprise not found with id " + id);
        }
        drTypeEnterpriseRepository.deleteById(id);
    }
}

package kg.home.demo.service.impl;

import kg.home.demo.entity.DrEnterpriseEntity;
import kg.home.demo.mapper.DrEnterpriseMapper;
import kg.home.demo.model.DrEnterprise;
import kg.home.demo.model.DrEnterpriseRequest;
import kg.home.demo.repository.DrEnterpriseRepository;
import kg.home.demo.service.DrEnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrEnterpriseServiceImpl implements DrEnterpriseService {


    private final DrEnterpriseRepository drEnterpriseRepository;
    private final DrEnterpriseMapper mapper;

    @Override
    public List<DrEnterprise> getAllDrEnterprises() {
        return drEnterpriseRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<DrEnterprise> getDrEnterpriseById(Long id) {
        return drEnterpriseRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrEnterprise createDrEnterprise(DrEnterpriseRequest request) {
        DrEnterpriseEntity entity = new DrEnterpriseEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(drEnterpriseRepository.save(entity));
    }

    @Override
    @Transactional
    public DrEnterprise updateDrEnterprise(Long id, DrEnterpriseRequest request) {
        DrEnterpriseEntity entity = drEnterpriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DrEnterpriseEntity not found with id " + id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(drEnterpriseRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDrEnterprise(Long id) {
        if (!drEnterpriseRepository.existsById(id)) {
            throw new RuntimeException("DrEnterpriseEntity not found with id " + id);
        }
        drEnterpriseRepository.deleteById(id);
    }

}

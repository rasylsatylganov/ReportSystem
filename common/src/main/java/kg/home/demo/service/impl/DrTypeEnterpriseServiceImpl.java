package kg.home.demo.service.impl;

import kg.home.demo.entity.DrTypeEnterpriseEntity;
import kg.home.demo.mapper.DrTypeEnterpriseMapper;
import kg.home.demo.model.DrTypeEnterprise;
import kg.home.demo.model.DrTypeEnterpriseRequest;
import kg.home.demo.repository.DrTypeEnterpriseRepository;
import kg.home.demo.service.DrTypeEnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrTypeEnterpriseServiceImpl implements DrTypeEnterpriseService {


    private final DrTypeEnterpriseRepository drTypeEnterpriseRepository;
    private final DrTypeEnterpriseMapper mapper;

    @Override
    public List<DrTypeEnterprise> getAllDrTypeEnterprises() {
        return drTypeEnterpriseRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<DrTypeEnterprise> getDrTypeEnterpriseById(Long id) {
        return drTypeEnterpriseRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrTypeEnterprise createDrTypeEnterprise(DrTypeEnterpriseRequest request) {
        DrTypeEnterpriseEntity entity = new DrTypeEnterpriseEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(drTypeEnterpriseRepository.save(entity));
    }

    @Override
    @Transactional
    public DrTypeEnterprise updateDrTypeEnterprise(Long id, DrTypeEnterpriseRequest request) {
        DrTypeEnterpriseEntity entity = drTypeEnterpriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DrTypeEnterpriseEntity not found with id " + id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(drTypeEnterpriseRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDrTypeEnterprise(Long id) {
        if (!drTypeEnterpriseRepository.existsById(id)) {
            throw new RuntimeException("DrTypeEnterpriseEntity not found with id " + id);
        }
        drTypeEnterpriseRepository.deleteById(id);
    }

}

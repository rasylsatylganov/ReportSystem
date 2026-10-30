package kg.home.demo.service.impl;

import kg.home.demo.entity.DrTypeEnterpriseEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrTypeEnterpriseMapper;
import kg.home.demo.model.DrTypeEnterprise;
import kg.home.demo.model.DrTypeEnterpriseRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.repository.DrTypeEnterpriseRepository;
import kg.home.demo.service.DrTypeEnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrTypeEnterpriseServiceImpl implements DrTypeEnterpriseService {


    private final DrTypeEnterpriseRepository drTypeEnterpriseRepository;
    private final DrTypeEnterpriseMapper mapper;

    @Override
    public PageResult<DrTypeEnterprise> findAllDrTypeEnterprises(Pageable pageable) {
        return PageResult.from(drTypeEnterpriseRepository.findAll(pageable).map(mapper::toModel));
    }

    @Override
    public Optional<DrTypeEnterprise> findDrTypeEnterpriseById(Long id) {
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
                .orElseThrow(() -> new ResourceNotFoundException("Тип предприятия", id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(drTypeEnterpriseRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDrTypeEnterprise(Long id) {
        if (!drTypeEnterpriseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Тип предприятия", id);
        }
        drTypeEnterpriseRepository.deleteById(id);
    }

}

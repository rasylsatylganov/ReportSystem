package kg.home.demo.service.impl;

import kg.home.demo.entity.TbReferenceItemValueEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.TbReferenceItemValueMapper;
import kg.home.demo.model.TbReferenceItemValue;
import kg.home.demo.model.TbReferenceItemValueRequest;
import kg.home.demo.repository.TbReferenceItemValueRepository;
import kg.home.demo.service.TbReferenceItemValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TbReferenceItemValueServiceImpl implements TbReferenceItemValueService {


    private final TbReferenceItemValueRepository tbReferenceItemValueRepository;
    private final TbReferenceItemValueMapper mapper;

    @Override
    public List<TbReferenceItemValue> getAllTbReferenceItemValues() {
        return tbReferenceItemValueRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<TbReferenceItemValue> getTbReferenceItemValueById(Long id) {
        return tbReferenceItemValueRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public TbReferenceItemValue createTbReferenceItemValue(TbReferenceItemValueRequest request) {
        TbReferenceItemValueEntity entity = new TbReferenceItemValueEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(tbReferenceItemValueRepository.save(entity));
    }

    @Override
    @Transactional
    public TbReferenceItemValue updateTbReferenceItemValue(Long id, TbReferenceItemValueRequest request) {
        TbReferenceItemValueEntity entity = tbReferenceItemValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Значение отчета", id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(tbReferenceItemValueRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteTbReferenceItemValue(Long id) {
        if (!tbReferenceItemValueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Значение отчета", id);
        }
        tbReferenceItemValueRepository.deleteById(id);
    }

}

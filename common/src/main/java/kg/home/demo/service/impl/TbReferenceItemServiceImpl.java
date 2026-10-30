package kg.home.demo.service.impl;

import kg.home.demo.entity.TbReferenceItemEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.TbReferenceItemMapper;
import kg.home.demo.model.TbReferenceItem;
import kg.home.demo.model.TbReferenceItemRequest;
import kg.home.demo.repository.TbReferenceItemRepository;
import kg.home.demo.service.TbReferenceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TbReferenceItemServiceImpl implements TbReferenceItemService {


    private final TbReferenceItemRepository tbReferenceItemRepository;
    private final TbReferenceItemMapper mapper;

    @Override
    public List<TbReferenceItem> getAllTbReferenceItems() {
        return tbReferenceItemRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<TbReferenceItem> getTbReferenceItemById(Long id) {
        return tbReferenceItemRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public TbReferenceItem createTbReferenceItem(TbReferenceItemRequest request) {
        TbReferenceItemEntity entity = new TbReferenceItemEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(tbReferenceItemRepository.save(entity));
    }

    @Override
    @Transactional
    public TbReferenceItem updateTbReferenceItem(Long id, TbReferenceItemRequest request) {
        TbReferenceItemEntity entity = tbReferenceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Данные справочника", id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(tbReferenceItemRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteTbReferenceItem(Long id) {
        if (!tbReferenceItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Данные справочника", id);
        }
        tbReferenceItemRepository.deleteById(id);
    }

}

package kg.home.demo.service.impl;

import kg.home.demo.entity.MdReportInReferenceItemEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.MdReportInReferenceItemMapper;
import kg.home.demo.model.MdReportInReferenceItem;
import kg.home.demo.model.MdReportInReferenceItemRequest;
import kg.home.demo.repository.MdReportInReferenceItemRepository;
import kg.home.demo.service.MdReportInReferenceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MdReportInReferenceItemServiceImpl implements MdReportInReferenceItemService {


    private final MdReportInReferenceItemRepository mdReportInReferenceItemRepository;
    private final MdReportInReferenceItemMapper mapper;

    @Override
    public List<MdReportInReferenceItem> getAllMdReportInReferenceItems() {
        return mdReportInReferenceItemRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<MdReportInReferenceItem> getMdReportInReferenceItemById(Long id) {
        return mdReportInReferenceItemRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public MdReportInReferenceItem createMdReportInReferenceItem(MdReportInReferenceItemRequest request) {
        MdReportInReferenceItemEntity entity = new MdReportInReferenceItemEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(mdReportInReferenceItemRepository.save(entity));
    }

    @Override
    @Transactional
    public MdReportInReferenceItem updateMdReportInReferenceItem(Long id, MdReportInReferenceItemRequest request) {
        MdReportInReferenceItemEntity entity = mdReportInReferenceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Поле входящего отчета", id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(mdReportInReferenceItemRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteMdReportInReferenceItem(Long id) {
        if (!mdReportInReferenceItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Поле входящего отчета", id);
        }
        mdReportInReferenceItemRepository.deleteById(id);
    }

}

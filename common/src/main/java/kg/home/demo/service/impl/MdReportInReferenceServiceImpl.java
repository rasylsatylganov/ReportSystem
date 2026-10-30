package kg.home.demo.service.impl;

import kg.home.demo.entity.MdReportInReferenceEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.MdReportInReferenceMapper;
import kg.home.demo.model.MdReportInReference;
import kg.home.demo.model.MdReportInReferenceRequest;
import kg.home.demo.repository.MdReportInReferenceRepository;
import kg.home.demo.service.MdReportInReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MdReportInReferenceServiceImpl implements MdReportInReferenceService {


    private final MdReportInReferenceRepository mdReportInReferenceRepository;
    private final MdReportInReferenceMapper mapper;

    @Override
    public List<MdReportInReference> getAllMdReportInReferences() {
        return mdReportInReferenceRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<MdReportInReference> getMdReportInReferenceById(Long id) {
        return mdReportInReferenceRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public MdReportInReference createMdReportInReference(MdReportInReferenceRequest request) {
        MdReportInReferenceEntity entity = new MdReportInReferenceEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(mdReportInReferenceRepository.save(entity));
    }

    @Override
    @Transactional
    public MdReportInReference updateMdReportInReference(Long id, MdReportInReferenceRequest request) {
        MdReportInReferenceEntity entity = mdReportInReferenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Справочник входящего отчета", id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(mdReportInReferenceRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteMdReportInReference(Long id) {
        if (!mdReportInReferenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Справочник входящего отчета", id);
        }
        mdReportInReferenceRepository.deleteById(id);
    }

}

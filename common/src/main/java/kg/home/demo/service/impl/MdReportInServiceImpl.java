package kg.home.demo.service.impl;

import kg.home.demo.entity.MdReportInEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.MdReportInMapper;
import kg.home.demo.model.MdReportIn;
import kg.home.demo.model.MdReportInRequest;
import kg.home.demo.repository.MdReportInRepository;
import kg.home.demo.service.MdReportInService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MdReportInServiceImpl implements MdReportInService {


    private final MdReportInRepository mdReportInRepository;
    private final MdReportInMapper mapper;

    @Override
    public List<MdReportIn> findAllMdReportIns() {
        return mdReportInRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<MdReportIn> findMdReportInById(Long id) {
        return mdReportInRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public MdReportIn createMdReportIn(MdReportInRequest request) {
        MdReportInEntity entity = new MdReportInEntity();
        mapper.applyRequest(entity, request);
        return mapper.toModel(mdReportInRepository.save(entity));
    }

    @Override
    @Transactional
    public MdReportIn updateMdReportIn(Long id, MdReportInRequest request) {
        MdReportInEntity entity = mdReportInRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Входящий отчет", id));
        mapper.applyRequest(entity, request);
        return mapper.toModel(mdReportInRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteMdReportIn(Long id) {
        if (!mdReportInRepository.existsById(id)) {
            throw new ResourceNotFoundException("Входящий отчет", id);
        }
        mdReportInRepository.deleteById(id);
    }

}

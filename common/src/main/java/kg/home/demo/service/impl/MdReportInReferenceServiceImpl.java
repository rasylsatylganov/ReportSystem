package kg.home.demo.service.impl;

import kg.home.demo.entity.MdReportInReferenceEntity;
import kg.home.demo.repository.MdReportInReferenceRepository;
import kg.home.demo.service.MdReportInReferenceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MdReportInReferenceServiceImpl implements MdReportInReferenceService {

    private final MdReportInReferenceRepository mdReportInReferenceRepository;

    public MdReportInReferenceServiceImpl(MdReportInReferenceRepository mdReportInReferenceRepository) {
        this.mdReportInReferenceRepository = mdReportInReferenceRepository;
    }

    @Override
    public List<MdReportInReferenceEntity> getAllMdReportInReferences() {
        return mdReportInReferenceRepository.findAll();
    }

    @Override
    public Optional<MdReportInReferenceEntity> getMdReportInReferenceById(Long id) {
        return mdReportInReferenceRepository.findById(id);
    }

    @Override
    public MdReportInReferenceEntity createMdReportInReference(MdReportInReferenceEntity mdReportInReference) {
        return mdReportInReferenceRepository.save(mdReportInReference);
    }

    @Override
    public MdReportInReferenceEntity updateMdReportInReference(Long id, MdReportInReferenceEntity mdReportInReference) {
        return mdReportInReferenceRepository.findById(id)
                .map(existing -> {
                    existing.setReportIn(mdReportInReference.getReportIn());
                    existing.setReference(mdReportInReference.getReference());
                    existing.setTypePosition(mdReportInReference.getTypePosition());
                    existing.setIndexNum(mdReportInReference.getIndexNum());
                    existing.setOrderSort(mdReportInReference.getOrderSort());
                    return mdReportInReferenceRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("MdReportInReferenceEntity not found with id " + id));
    }

    @Override
    public void deleteMdReportInReference(Long id) {
        if (!mdReportInReferenceRepository.existsById(id)) {
            throw new RuntimeException("MdReportInReferenceEntity not found with id " + id);
        }
        mdReportInReferenceRepository.deleteById(id);
    }
}

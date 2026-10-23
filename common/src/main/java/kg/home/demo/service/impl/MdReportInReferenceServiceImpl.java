package kg.home.demo.service.impl;

import kg.home.demo.entity.MdReportInReference;
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
    public List<MdReportInReference> getAllMdReportInReferences() {
        return mdReportInReferenceRepository.findAll();
    }

    @Override
    public Optional<MdReportInReference> getMdReportInReferenceById(Long id) {
        return mdReportInReferenceRepository.findById(id);
    }

    @Override
    public MdReportInReference createMdReportInReference(MdReportInReference mdReportInReference) {
        return mdReportInReferenceRepository.save(mdReportInReference);
    }

    @Override
    public MdReportInReference updateMdReportInReference(Long id, MdReportInReference mdReportInReference) {
        return mdReportInReferenceRepository.findById(id)
                .map(existing -> {
                    existing.setReportIn(mdReportInReference.getReportIn());
                    existing.setReference(mdReportInReference.getReference());
                    existing.setTypePosition(mdReportInReference.getTypePosition());
                    existing.setIndexNum(mdReportInReference.getIndexNum());
                    existing.setOrderSort(mdReportInReference.getOrderSort());
                    return mdReportInReferenceRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("MdReportInReference not found with id " + id));
    }

    @Override
    public void deleteMdReportInReference(Long id) {
        if (!mdReportInReferenceRepository.existsById(id)) {
            throw new RuntimeException("MdReportInReference not found with id " + id);
        }
        mdReportInReferenceRepository.deleteById(id);
    }
}

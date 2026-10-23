package kg.home.demo.service.impl;

import kg.home.demo.entity.MdReportIn;
import kg.home.demo.repository.MdReportInRepository;
import kg.home.demo.service.MdReportInService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MdReportInServiceImpl implements MdReportInService {

    private final MdReportInRepository mdReportInRepository;

    public MdReportInServiceImpl(MdReportInRepository mdReportInRepository) {
        this.mdReportInRepository = mdReportInRepository;
    }

    @Override
    public List<MdReportIn> getAllMdReportIns() {
        return mdReportInRepository.findAll();
    }

    @Override
    public Optional<MdReportIn> getMdReportInById(Long id) {
        return mdReportInRepository.findById(id);
    }

    @Override
    public MdReportIn createMdReportIn(MdReportIn mdReportIn) {
        return mdReportInRepository.save(mdReportIn);
    }

    @Override
    public MdReportIn updateMdReportIn(Long id, MdReportIn mdReportIn) {
        return mdReportInRepository.findById(id)
                .map(existing -> {
                    existing.setCodeReportIn(mdReportIn.getCodeReportIn());
                    existing.setNameReportIn(mdReportIn.getNameReportIn());
                    existing.setTypePeriod(mdReportIn.getTypePeriod());
                    existing.setTypeEnterprise(mdReportIn.getTypeEnterprise());
                    existing.setIndexRowScanBegin(mdReportIn.getIndexRowScanBegin());
                    existing.setIndexRowScanEnd(mdReportIn.getIndexRowScanEnd());
                    return mdReportInRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("MdReportIn not found with id " + id));
    }

    @Override
    public void deleteMdReportIn(Long id) {
        if (!mdReportInRepository.existsById(id)) {
            throw new RuntimeException("MdReportIn not found with id " + id);
        }
        mdReportInRepository.deleteById(id);
    }
}

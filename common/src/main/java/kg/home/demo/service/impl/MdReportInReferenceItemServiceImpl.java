package kg.home.demo.service.impl;

import kg.home.demo.entity.MdReportInReferenceItem;
import kg.home.demo.repository.MdReportInReferenceItemRepository;
import kg.home.demo.service.MdReportInReferenceItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MdReportInReferenceItemServiceImpl implements MdReportInReferenceItemService {

    private final MdReportInReferenceItemRepository mdReportInReferenceItemRepository;

    public MdReportInReferenceItemServiceImpl(MdReportInReferenceItemRepository mdReportInReferenceItemRepository) {
        this.mdReportInReferenceItemRepository = mdReportInReferenceItemRepository;
    }

    @Override
    public List<MdReportInReferenceItem> getAllMdReportInReferenceItems() {
        return mdReportInReferenceItemRepository.findAll();
    }

    @Override
    public Optional<MdReportInReferenceItem> getMdReportInReferenceItemById(Long id) {
        return mdReportInReferenceItemRepository.findById(id);
    }

    @Override
    public MdReportInReferenceItem createMdReportInReferenceItem(MdReportInReferenceItem mdReportInReferenceItem) {
        return mdReportInReferenceItemRepository.save(mdReportInReferenceItem);
    }

    @Override
    public MdReportInReferenceItem updateMdReportInReferenceItem(Long id, MdReportInReferenceItem mdReportInReferenceItem) {
        return mdReportInReferenceItemRepository.findById(id)
                .map(existing -> {
                    existing.setReportIn(mdReportInReferenceItem.getReportIn());
                    existing.setReferenceItem(mdReportInReferenceItem.getReferenceItem());
                    existing.setTypeFieldValue(mdReportInReferenceItem.getTypeFieldValue());
                    existing.setIndexColumn(mdReportInReferenceItem.getIndexColumn());
                    existing.setOrderSort(mdReportInReferenceItem.getOrderSort());
                    existing.setIsRequired(mdReportInReferenceItem.getIsRequired());
                    return mdReportInReferenceItemRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("MdReportInReferenceItem not found with id " + id));
    }

    @Override
    public void deleteMdReportInReferenceItem(Long id) {
        if (!mdReportInReferenceItemRepository.existsById(id)) {
            throw new RuntimeException("MdReportInReferenceItem not found with id " + id);
        }
        mdReportInReferenceItemRepository.deleteById(id);
    }
}

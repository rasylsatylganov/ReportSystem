package kg.home.demo.service.impl;

import kg.home.demo.entity.TbReferenceItem;
import kg.home.demo.repository.TbReferenceItemRepository;
import kg.home.demo.service.TbReferenceItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TbReferenceItemServiceImpl implements TbReferenceItemService {

    private final TbReferenceItemRepository tbReferenceItemRepository;

    public TbReferenceItemServiceImpl(TbReferenceItemRepository tbReferenceItemRepository) {
        this.tbReferenceItemRepository = tbReferenceItemRepository;
    }

    @Override
    public List<TbReferenceItem> getAllTbReferenceItems() {
        return tbReferenceItemRepository.findAll();
    }

    @Override
    public Optional<TbReferenceItem> getTbReferenceItemById(Long id) {
        return tbReferenceItemRepository.findById(id);
    }

    @Override
    public TbReferenceItem createTbReferenceItem(TbReferenceItem tbReferenceItem) {
        return tbReferenceItemRepository.save(tbReferenceItem);
    }

    @Override
    public TbReferenceItem updateTbReferenceItem(Long id, TbReferenceItem tbReferenceItem) {
        return tbReferenceItemRepository.findById(id)
                .map(existing -> {
                    existing.setPeriod(tbReferenceItem.getPeriod());
                    existing.setEnterprise(tbReferenceItem.getEnterprise());
                    existing.setReferenceItem(tbReferenceItem.getReferenceItem());
                    existing.setReportInReference(tbReferenceItem.getReportInReference());
                    existing.setIndexRow(tbReferenceItem.getIndexRow());
                    return tbReferenceItemRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("TbReferenceItem not found with id " + id));
    }

    @Override
    public void deleteTbReferenceItem(Long id) {
        if (!tbReferenceItemRepository.existsById(id)) {
            throw new RuntimeException("TbReferenceItem not found with id " + id);
        }
        tbReferenceItemRepository.deleteById(id);
    }
}

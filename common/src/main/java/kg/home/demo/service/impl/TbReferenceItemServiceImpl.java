package kg.home.demo.service.impl;

import kg.home.demo.entity.TbReferenceItemEntity;
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
    public List<TbReferenceItemEntity> getAllTbReferenceItems() {
        return tbReferenceItemRepository.findAll();
    }

    @Override
    public Optional<TbReferenceItemEntity> getTbReferenceItemById(Long id) {
        return tbReferenceItemRepository.findById(id);
    }

    @Override
    public TbReferenceItemEntity createTbReferenceItem(TbReferenceItemEntity tbReferenceItem) {
        return tbReferenceItemRepository.save(tbReferenceItem);
    }

    @Override
    public TbReferenceItemEntity updateTbReferenceItem(Long id, TbReferenceItemEntity tbReferenceItem) {
        return tbReferenceItemRepository.findById(id)
                .map(existing -> {
                    existing.setPeriod(tbReferenceItem.getPeriod());
                    existing.setEnterprise(tbReferenceItem.getEnterprise());
                    existing.setReferenceItem(tbReferenceItem.getReferenceItem());
                    existing.setReportInReference(tbReferenceItem.getReportInReference());
                    existing.setIndexRow(tbReferenceItem.getIndexRow());
                    return tbReferenceItemRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("TbReferenceItemEntity not found with id " + id));
    }

    @Override
    public void deleteTbReferenceItem(Long id) {
        if (!tbReferenceItemRepository.existsById(id)) {
            throw new RuntimeException("TbReferenceItemEntity not found with id " + id);
        }
        tbReferenceItemRepository.deleteById(id);
    }
}

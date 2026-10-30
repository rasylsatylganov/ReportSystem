package kg.home.demo.service.impl;

import kg.home.demo.entity.DrReferenceItemEntity;
import kg.home.demo.repository.DrReferenceItemRepository;
import kg.home.demo.service.DrReferenceItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DrReferenceItemServiceImpl implements DrReferenceItemService {

    private final DrReferenceItemRepository drReferenceItemRepository;

    public DrReferenceItemServiceImpl(DrReferenceItemRepository drReferenceItemRepository) {
        this.drReferenceItemRepository = drReferenceItemRepository;
    }

    @Override
    public List<DrReferenceItemEntity> getAllDrReferenceItems() {
        return drReferenceItemRepository.findAll();
    }

    @Override
    public Optional<DrReferenceItemEntity> getDrReferenceItemById(Long id) {
        return drReferenceItemRepository.findById(id);
    }

    @Override
    public DrReferenceItemEntity createDrReferenceItem(DrReferenceItemEntity drReferenceItem) {
        return drReferenceItemRepository.save(drReferenceItem);
    }

    @Override
    public DrReferenceItemEntity updateDrReferenceItem(Long id, DrReferenceItemEntity drReferenceItem) {
        return drReferenceItemRepository.findById(id)
                .map(existing -> {
                    existing.setReference(drReferenceItem.getReference());
                    existing.setCodeReferenceItem(drReferenceItem.getCodeReferenceItem());
                    existing.setNameReferenceItem(drReferenceItem.getNameReferenceItem());
                    existing.setOrderSort(drReferenceItem.getOrderSort());
                    return drReferenceItemRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DrReferenceItemEntity not found with id " + id));
    }

    @Override
    public void deleteDrReferenceItem(Long id) {
        if (!drReferenceItemRepository.existsById(id)) {
            throw new RuntimeException("DrReferenceItemEntity not found with id " + id);
        }
        drReferenceItemRepository.deleteById(id);
    }
}

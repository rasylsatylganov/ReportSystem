package kg.home.demo.service.impl;

import kg.home.demo.entity.DrReferenceItem;
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
    public List<DrReferenceItem> getAllDrReferenceItems() {
        return drReferenceItemRepository.findAll();
    }

    @Override
    public Optional<DrReferenceItem> getDrReferenceItemById(Long id) {
        return drReferenceItemRepository.findById(id);
    }

    @Override
    public DrReferenceItem createDrReferenceItem(DrReferenceItem drReferenceItem) {
        return drReferenceItemRepository.save(drReferenceItem);
    }

    @Override
    public DrReferenceItem updateDrReferenceItem(Long id, DrReferenceItem drReferenceItem) {
        return drReferenceItemRepository.findById(id)
                .map(existing -> {
                    existing.setReference(drReferenceItem.getReference());
                    existing.setCodeReferenceItem(drReferenceItem.getCodeReferenceItem());
                    existing.setNameReferenceItem(drReferenceItem.getNameReferenceItem());
                    existing.setOrderSort(drReferenceItem.getOrderSort());
                    return drReferenceItemRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DrReferenceItem not found with id " + id));
    }

    @Override
    public void deleteDrReferenceItem(Long id) {
        if (!drReferenceItemRepository.existsById(id)) {
            throw new RuntimeException("DrReferenceItem not found with id " + id);
        }
        drReferenceItemRepository.deleteById(id);
    }
}

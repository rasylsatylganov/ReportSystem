package kg.home.demo.service.impl;

import kg.home.demo.entity.DrEnterpriseEntity;
import kg.home.demo.exception.ResourceNotFoundException;
import kg.home.demo.mapper.DrEnterpriseMapper;
import kg.home.demo.model.DrEnterprise;
import kg.home.demo.model.DrEnterpriseRequest;
import kg.home.demo.model.PageResult;
import kg.home.demo.repository.DrEnterpriseRepository;
import kg.home.demo.service.DrEnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrEnterpriseServiceImpl implements DrEnterpriseService {


    private final DrEnterpriseRepository drEnterpriseRepository;
    private final DrEnterpriseMapper mapper;

    @Override
    public PageResult<DrEnterprise> findAllDrEnterprises(Pageable pageable) {
        return PageResult.from(drEnterpriseRepository.findAll(pageable).map(mapper::toModel));
    }

    @Override
    public PageResult<DrEnterprise> findDrEnterprisesByType(Long typeEnterpriseId, Pageable pageable) {
        return PageResult.from(drEnterpriseRepository.findByTypeEnterprise_Id(typeEnterpriseId, pageable)
                .map(mapper::toModel));
    }

    @Override
    public Optional<DrEnterprise> findDrEnterpriseById(Long id) {
        return drEnterpriseRepository.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public DrEnterprise createDrEnterprise(DrEnterpriseRequest request) {
        drEnterpriseRepository.findAllForUpdate();
        DrEnterpriseEntity entity = new DrEnterpriseEntity();
        mapper.applyRequest(entity, request);
        entity.setSortIndex(drEnterpriseRepository.nextSortIndex());
        return mapper.toModel(drEnterpriseRepository.save(entity));
    }

    @Override
    @Transactional
    public DrEnterprise updateDrEnterprise(Long id, DrEnterpriseRequest request) {
        DrEnterpriseEntity entity = findRequired(drEnterpriseRepository.findAllForUpdate(), id);
        mapper.applyRequest(entity, request);
        return mapper.toModel(drEnterpriseRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDrEnterprise(Long id) {
        drEnterpriseRepository.delete(findRequired(drEnterpriseRepository.findAllForUpdate(), id));
    }

    @Override
    @Transactional
    public void moveUp(Long id, Long typeEnterpriseId) {
        move(id, typeEnterpriseId, -1);
    }

    @Override
    @Transactional
    public void moveDown(Long id, Long typeEnterpriseId) {
        move(id, typeEnterpriseId, 1);
    }

    private void move(Long id, Long typeEnterpriseId, int offset) {
        List<DrEnterpriseEntity> locked = drEnterpriseRepository.findAllForUpdate();
        DrEnterpriseEntity current = findRequired(locked, id);
        if (typeEnterpriseId != null && !current.getTypeEnterprise().getId().equals(typeEnterpriseId)) {
            throw new ResourceNotFoundException("Предприятие выбранного типа", id);
        }
        List<DrEnterpriseEntity> ordered = locked.stream()
                .filter(enterprise -> typeEnterpriseId == null ||
                        enterprise.getTypeEnterprise().getId().equals(typeEnterpriseId))
                .sorted(Comparator.comparing(DrEnterpriseEntity::getSortIndex).thenComparing(DrEnterpriseEntity::getId))
                .toList();
        int neighborIndex = ordered.indexOf(current) + offset;
        if (neighborIndex < 0 || neighborIndex >= ordered.size()) return;
        DrEnterpriseEntity neighbor = ordered.get(neighborIndex);
        Long previousIndex = current.getSortIndex();
        current.setSortIndex(neighbor.getSortIndex());
        neighbor.setSortIndex(previousIndex);
        drEnterpriseRepository.saveAll(List.of(current, neighbor));
        drEnterpriseRepository.flush();
    }

    private DrEnterpriseEntity findRequired(List<DrEnterpriseEntity> enterprises, Long id) {
        return enterprises.stream().filter(enterprise -> enterprise.getId().equals(id)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Предприятие", id));
    }

}

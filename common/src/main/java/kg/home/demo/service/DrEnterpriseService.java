package kg.home.demo.service;

import kg.home.demo.model.DrEnterprise;
import kg.home.demo.model.DrEnterpriseRequest;
import kg.home.demo.model.PageResult;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DrEnterpriseService {

    PageResult<DrEnterprise> findAllDrEnterprises(Pageable pageable);

    PageResult<DrEnterprise> findDrEnterprisesByType(Long typeEnterpriseId, Pageable pageable);

    Optional<DrEnterprise> findDrEnterpriseById(Long id);

    DrEnterprise createDrEnterprise(DrEnterpriseRequest request);

    DrEnterprise updateDrEnterprise(Long id, DrEnterpriseRequest request);

    void deleteDrEnterprise(Long id);

    void moveUp(Long id, Long typeEnterpriseId);

    void moveDown(Long id, Long typeEnterpriseId);
}

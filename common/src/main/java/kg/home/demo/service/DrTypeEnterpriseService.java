package kg.home.demo.service;

import kg.home.demo.model.DrTypeEnterprise;
import kg.home.demo.model.DrTypeEnterpriseRequest;
import kg.home.demo.model.PageResult;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DrTypeEnterpriseService {

    PageResult<DrTypeEnterprise> findAllDrTypeEnterprises(Pageable pageable);

    Optional<DrTypeEnterprise> findDrTypeEnterpriseById(Long id);

    DrTypeEnterprise createDrTypeEnterprise(DrTypeEnterpriseRequest request);

    DrTypeEnterprise updateDrTypeEnterprise(Long id, DrTypeEnterpriseRequest request);

    void deleteDrTypeEnterprise(Long id);

    void moveUp(Long id);

    void moveDown(Long id);
}

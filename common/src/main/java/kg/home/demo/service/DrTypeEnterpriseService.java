package kg.home.demo.service;

import kg.home.demo.model.DrTypeEnterprise;
import kg.home.demo.model.DrTypeEnterpriseRequest;

import java.util.List;
import java.util.Optional;

public interface DrTypeEnterpriseService {

    List<DrTypeEnterprise> getAllDrTypeEnterprises();

    Optional<DrTypeEnterprise> getDrTypeEnterpriseById(Long id);

    DrTypeEnterprise createDrTypeEnterprise(DrTypeEnterpriseRequest request);

    DrTypeEnterprise updateDrTypeEnterprise(Long id, DrTypeEnterpriseRequest request);

    void deleteDrTypeEnterprise(Long id);
}

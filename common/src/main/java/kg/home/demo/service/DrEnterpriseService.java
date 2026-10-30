package kg.home.demo.service;

import kg.home.demo.model.DrEnterprise;
import kg.home.demo.model.DrEnterpriseRequest;

import java.util.List;
import java.util.Optional;

public interface DrEnterpriseService {

    List<DrEnterprise> findAllDrEnterprises();

    Optional<DrEnterprise> findDrEnterpriseById(Long id);

    DrEnterprise createDrEnterprise(DrEnterpriseRequest request);

    DrEnterprise updateDrEnterprise(Long id, DrEnterpriseRequest request);

    void deleteDrEnterprise(Long id);
}

package kg.home.demo.service;

import kg.home.demo.entity.DrEnterprise;

import java.util.List;
import java.util.Optional;

public interface DrEnterpriseService {

    List<DrEnterprise> getAllDrEnterprises();

    Optional<DrEnterprise> getDrEnterpriseById(Long id);

    DrEnterprise createDrEnterprise(DrEnterprise drEnterprise);

    DrEnterprise updateDrEnterprise(Long id, DrEnterprise drEnterprise);

    void deleteDrEnterprise(Long id);
}

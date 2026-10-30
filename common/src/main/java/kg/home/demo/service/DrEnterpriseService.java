package kg.home.demo.service;

import kg.home.demo.entity.DrEnterpriseEntity;

import java.util.List;
import java.util.Optional;

public interface DrEnterpriseService {

    List<DrEnterpriseEntity> getAllDrEnterprises();

    Optional<DrEnterpriseEntity> getDrEnterpriseById(Long id);

    DrEnterpriseEntity createDrEnterprise(DrEnterpriseEntity drEnterprise);

    DrEnterpriseEntity updateDrEnterprise(Long id, DrEnterpriseEntity drEnterprise);

    void deleteDrEnterprise(Long id);
}

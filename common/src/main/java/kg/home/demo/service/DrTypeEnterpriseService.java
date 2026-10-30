package kg.home.demo.service;

import kg.home.demo.entity.DrTypeEnterpriseEntity;

import java.util.List;
import java.util.Optional;

public interface DrTypeEnterpriseService {

    List<DrTypeEnterpriseEntity> getAllDrTypeEnterprises();

    Optional<DrTypeEnterpriseEntity> getDrTypeEnterpriseById(Long id);

    DrTypeEnterpriseEntity createDrTypeEnterprise(DrTypeEnterpriseEntity drTypeEnterprise);

    DrTypeEnterpriseEntity updateDrTypeEnterprise(Long id, DrTypeEnterpriseEntity drTypeEnterprise);

    void deleteDrTypeEnterprise(Long id);
}

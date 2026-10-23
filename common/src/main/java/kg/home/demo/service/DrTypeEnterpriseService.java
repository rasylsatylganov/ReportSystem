package kg.home.demo.service;

import kg.home.demo.entity.DrTypeEnterprise;

import java.util.List;
import java.util.Optional;

public interface DrTypeEnterpriseService {

    List<DrTypeEnterprise> getAllDrTypeEnterprises();

    Optional<DrTypeEnterprise> getDrTypeEnterpriseById(Long id);

    DrTypeEnterprise createDrTypeEnterprise(DrTypeEnterprise drTypeEnterprise);

    DrTypeEnterprise updateDrTypeEnterprise(Long id, DrTypeEnterprise drTypeEnterprise);

    void deleteDrTypeEnterprise(Long id);
}

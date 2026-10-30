package kg.home.demo.service;

import kg.home.demo.entity.DrTypeFieldEntity;

import java.util.List;
import java.util.Optional;

public interface DrTypeFieldService {

    List<DrTypeFieldEntity> getAllDrTypeFields();

    Optional<DrTypeFieldEntity> getDrTypeFieldById(Long id);

    DrTypeFieldEntity createDrTypeField(DrTypeFieldEntity drTypeField);

    DrTypeFieldEntity updateDrTypeField(Long id, DrTypeFieldEntity drTypeField);

    void deleteDrTypeField(Long id);
}

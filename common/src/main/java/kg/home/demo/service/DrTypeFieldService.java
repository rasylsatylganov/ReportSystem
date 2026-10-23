package kg.home.demo.service;

import kg.home.demo.entity.DrTypeField;

import java.util.List;
import java.util.Optional;

public interface DrTypeFieldService {

    List<DrTypeField> getAllDrTypeFields();

    Optional<DrTypeField> getDrTypeFieldById(Long id);

    DrTypeField createDrTypeField(DrTypeField drTypeField);

    DrTypeField updateDrTypeField(Long id, DrTypeField drTypeField);

    void deleteDrTypeField(Long id);
}

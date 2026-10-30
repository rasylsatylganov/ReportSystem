package kg.home.demo.service;

import kg.home.demo.model.DrTypeField;
import kg.home.demo.model.DrTypeFieldRequest;

import java.util.List;
import java.util.Optional;

public interface DrTypeFieldService {

    List<DrTypeField> findAllDrTypeFields();

    Optional<DrTypeField> findDrTypeFieldById(Long id);

    DrTypeField createDrTypeField(DrTypeFieldRequest request);

    DrTypeField updateDrTypeField(Long id, DrTypeFieldRequest request);

    void deleteDrTypeField(Long id);
}

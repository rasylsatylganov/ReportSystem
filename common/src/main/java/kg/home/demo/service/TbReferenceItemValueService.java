package kg.home.demo.service;

import kg.home.demo.model.TbReferenceItemValue;
import kg.home.demo.model.TbReferenceItemValueRequest;

import java.util.List;
import java.util.Optional;

public interface TbReferenceItemValueService {

    List<TbReferenceItemValue> findAllTbReferenceItemValues();

    Optional<TbReferenceItemValue> findTbReferenceItemValueById(Long id);

    TbReferenceItemValue createTbReferenceItemValue(TbReferenceItemValueRequest request);

    TbReferenceItemValue updateTbReferenceItemValue(Long id, TbReferenceItemValueRequest request);

    void deleteTbReferenceItemValue(Long id);
}

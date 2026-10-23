package kg.home.demo.service;

import kg.home.demo.entity.TbReferenceItemValue;

import java.util.List;
import java.util.Optional;

public interface TbReferenceItemValueService {

    List<TbReferenceItemValue> getAllTbReferenceItemValues();

    Optional<TbReferenceItemValue> getTbReferenceItemValueById(Long id);

    TbReferenceItemValue createTbReferenceItemValue(TbReferenceItemValue tbReferenceItemValue);

    TbReferenceItemValue updateTbReferenceItemValue(Long id, TbReferenceItemValue tbReferenceItemValue);

    void deleteTbReferenceItemValue(Long id);
}

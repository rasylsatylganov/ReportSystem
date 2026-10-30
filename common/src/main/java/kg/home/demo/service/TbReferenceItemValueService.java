package kg.home.demo.service;

import kg.home.demo.entity.TbReferenceItemValueEntity;

import java.util.List;
import java.util.Optional;

public interface TbReferenceItemValueService {

    List<TbReferenceItemValueEntity> getAllTbReferenceItemValues();

    Optional<TbReferenceItemValueEntity> getTbReferenceItemValueById(Long id);

    TbReferenceItemValueEntity createTbReferenceItemValue(TbReferenceItemValueEntity tbReferenceItemValue);

    TbReferenceItemValueEntity updateTbReferenceItemValue(Long id, TbReferenceItemValueEntity tbReferenceItemValue);

    void deleteTbReferenceItemValue(Long id);
}

package kg.home.demo.service;

import kg.home.demo.entity.TbReferenceItemEntity;

import java.util.List;
import java.util.Optional;

public interface TbReferenceItemService {

    List<TbReferenceItemEntity> getAllTbReferenceItems();

    Optional<TbReferenceItemEntity> getTbReferenceItemById(Long id);

    TbReferenceItemEntity createTbReferenceItem(TbReferenceItemEntity tbReferenceItem);

    TbReferenceItemEntity updateTbReferenceItem(Long id, TbReferenceItemEntity tbReferenceItem);

    void deleteTbReferenceItem(Long id);
}

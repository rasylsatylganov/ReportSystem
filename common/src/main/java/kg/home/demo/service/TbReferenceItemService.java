package kg.home.demo.service;

import kg.home.demo.entity.TbReferenceItem;

import java.util.List;
import java.util.Optional;

public interface TbReferenceItemService {

    List<TbReferenceItem> getAllTbReferenceItems();

    Optional<TbReferenceItem> getTbReferenceItemById(Long id);

    TbReferenceItem createTbReferenceItem(TbReferenceItem tbReferenceItem);

    TbReferenceItem updateTbReferenceItem(Long id, TbReferenceItem tbReferenceItem);

    void deleteTbReferenceItem(Long id);
}

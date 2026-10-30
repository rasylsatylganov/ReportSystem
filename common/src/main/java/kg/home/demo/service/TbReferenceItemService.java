package kg.home.demo.service;

import kg.home.demo.model.TbReferenceItem;
import kg.home.demo.model.TbReferenceItemRequest;

import java.util.List;
import java.util.Optional;

public interface TbReferenceItemService {

    List<TbReferenceItem> findAllTbReferenceItems();

    Optional<TbReferenceItem> findTbReferenceItemById(Long id);

    TbReferenceItem createTbReferenceItem(TbReferenceItemRequest request);

    TbReferenceItem updateTbReferenceItem(Long id, TbReferenceItemRequest request);

    void deleteTbReferenceItem(Long id);
}

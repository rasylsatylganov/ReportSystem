package kg.home.demo.repository;

import kg.home.demo.entity.TbReferenceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TbReferenceItemRepository extends JpaRepository<TbReferenceItem, Long> {
}
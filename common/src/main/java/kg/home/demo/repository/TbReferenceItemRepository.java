package kg.home.demo.repository;

import kg.home.demo.entity.TbReferenceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TbReferenceItemRepository extends JpaRepository<TbReferenceItemEntity, Long> {
}
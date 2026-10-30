package kg.home.demo.repository;

import kg.home.demo.entity.DrReferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrReferenceRepository extends JpaRepository<DrReferenceEntity, Long> {
}
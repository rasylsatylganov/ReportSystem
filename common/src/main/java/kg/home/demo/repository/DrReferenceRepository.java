package kg.home.demo.repository;

import kg.home.demo.entity.DrReference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrReferenceRepository extends JpaRepository<DrReference, Long> {
}
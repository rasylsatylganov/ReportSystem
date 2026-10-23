package kg.home.demo.repository;

import kg.home.demo.entity.DrEnterprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrEnterpriseRepository extends JpaRepository<DrEnterprise, Long> {
}
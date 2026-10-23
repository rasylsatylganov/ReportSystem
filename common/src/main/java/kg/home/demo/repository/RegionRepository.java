package kg.home.demo.repository;

import kg.home.demo.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {

    // поиск по коду региона
    Region findByCodeRegion(String codeRegion);

    // поиск по имени региона
    Region findByNameRegion(String nameRegion);
}
package kg.home.demo.repository;

import kg.home.demo.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    // Поиск по коду страны
    Country findByCodeCountry(String codeCountry);

    // Поиск по имени страны
    List<Country> findByNameCountryContainingIgnoreCase(String nameCountry);

    // Поиск всех стран по ID региона
    List<Country> findByRegionId(Integer regionId);
}

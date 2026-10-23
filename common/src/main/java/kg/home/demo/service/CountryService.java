package kg.home.demo.service;

import kg.home.demo.entity.Country;

import java.util.List;
import java.util.Optional;

public interface CountryService {
    List<Country> getAllCountries();

    Optional<Country> getCountryById(Integer id);

    Country createCountry(Country country);

    Country updateCountry(Integer id, Country country);

    void deleteCountry(Integer id);
}

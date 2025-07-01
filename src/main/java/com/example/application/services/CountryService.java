package com.example.application.services;

import com.example.application.data.entity.Country;
import com.example.application.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CountryService {
    private final CountryRepository countryRepository;
    @Autowired
    public CountryService(CountryRepository countryRepository ) {
        this.countryRepository = countryRepository;
    }

    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    public Optional<Country> findById(Long id) {
        return countryRepository.findById(id.shortValue());
    }

    public Country save(Country country) {
        if (country.getLastUpdate() == null) {
            country.setLastUpdate(LocalDateTime.now());
        }
        return countryRepository.save(country);
    }

    public void delete(Integer idCountry) {
        countryRepository.deleteById(idCountry.shortValue());
    }

}

package com.example.application.services;

import com.example.application.data.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {
    private final CountryService countryService;
    @Autowired
    public CountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public List<Country> findAll() {
        return countryService.findAll();
    }

    public Optional<Country> findById(Long id) {
        return countryService.findById(id);
    }
    
}

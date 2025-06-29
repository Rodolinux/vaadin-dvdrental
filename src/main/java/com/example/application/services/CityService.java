package com.example.application.services;

import com.example.application.data.City;
import com.example.application.data.Film;
import com.example.application.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CityService {
    private final CityRepository cityRepository;

    @Autowired
    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }
    public Page<City> list(Pageable pageable) {
        return cityRepository.findAll(pageable);
    }
    public Page<City> findAll(Pageable pageable) {
        return cityRepository.findAll(pageable);
    }

    public long count() {
        return cityRepository.count();
    }

    public Optional<City> findById(Integer id) {
        return cityRepository.findById(id);
    }

    public City save(City city) {
        if (city.getLastUpdate() == null) {
            city.setLastUpdate(LocalDateTime.now());
        } else {
            city.setLastUpdate(city.getLastUpdate());
        }
        return cityRepository.save(city);
    }

    public void delete(Integer cityId) {
        cityRepository.deleteById(cityId);
    }

    public Page<City> findCitiesByCityName(String cityName, Pageable pageable) {
        return cityRepository.findByCityNameContainingIgnoreCase(cityName, pageable);
    }

}

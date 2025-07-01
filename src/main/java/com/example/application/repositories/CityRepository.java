package com.example.application.repositories;

import com.example.application.data.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {

    // Consulta personalizada para cargar City junto con Country para evitar LazyInitializationException
    @Query("SELECT c FROM City c JOIN FETCH c.countryId")
    Page<City> findAllWithCountry(Pageable pageable);

    @Query("SELECT c FROM City c JOIN FETCH c.countryId WHERE c.cityId = :cityId")
    Optional<City> findByIdWithCountry(Integer cityId);

    // Método de búsqueda por nombre de ciudad (case-insensitive)
    @Query("SELECT c FROM City c JOIN FETCH c.countryId WHERE LOWER(c.cityName) LIKE LOWER(CONCAT('%', :cityName, '%'))")
    Page<City> findByCityNameContainingIgnoreCase(String cityName, Pageable pageable);
}

// FilmRepository.java
package com.example.application.data;

import com.example.application.data.Film;
import com.example.application.data.MpaaRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Year;
import java.util.List;

import java.util.Optional;

// Repositorio de Spring Data JPA para la entidad Film
@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {

    // Ejemplo de método de consulta personalizado: buscar películas por título
    List<Film> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Ejemplo de método de consulta personalizado: buscar películas por año de lanzamiento
    List<Film> findByReleaseYear(Year year, Pageable pageable);

    // Ejemplo de método de consulta personalizado: buscar películas por clasificación (rating)
    List<Film> findByRating(MpaaRating rating, Pageable pageable);

    // Puedes añadir más métodos de consulta según tus necesidades
}


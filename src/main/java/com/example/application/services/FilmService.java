package com.example.application.services;

import com.example.application.data.entity.Film;
import com.example.application.repositories.FilmRepository;
import com.example.application.data.MpaaRating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

// Clase de servicio para manejar la lógica de negocio de la entidad Film
@Service
public class FilmService {

    private final FilmRepository filmRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    /**
     * Obtiene una página de películas.
     * @param pageable Objeto Pageable que contiene información de paginación (número de página, tamaño de página, ordenación).
     * @return Una página de películas.
     */
    public Page<Film> list(Pageable pageable) {
        return filmRepository.findAll(pageable);
    }

    /**
     * Obtiene el número total de películas.
     * @return El número total de películas en la base de datos.
     */
    public long count() {
        return filmRepository.count();
    }
    /**
     * Obtiene todas las películas.
     * @return Una lista de todas las películas.
     */
    public List<Film> getAllFilms(PageRequest springPageRequest) {
        return filmRepository.findAll();
    }

    /**
     * Obtiene todas las películas.
     * @return Una lista de todas las películas.
     */
    public List<Film> listAllFilms() {
        return filmRepository.findAll();
    }

    /**
     * Obtiene una película por su ID.
     * @param id El ID de la película.
     * @return Un Optional que contiene la película si se encuentra, o vacío si no.
     */
    public Optional<Film> getFilmById(Integer id) {
        return filmRepository.findById(id);
    }

    /**
     * Guarda una nueva película o actualiza una existente.
     * Si el filmId es nulo, se guarda como una nueva película.
     * Si el filmId existe, se actualiza la película existente.
     * La columna 'last_update' se establece automáticamente por la base de datos (trigger).
     * @param film La película a guardar/actualizar.
     * @return La película guardada/actualizada.
     */
    public Film saveFilm(Film film) {
        // Asegúrate de que lastUpdate no sea nulo si no se maneja por DB,
        // aunque el DDL tiene DEFAULT now() y un trigger.
        if (film.getLastUpdate() == null) {
            film.setLastUpdate(LocalDateTime.now());
        }
        return filmRepository.save(film);
    }

    /**
     * Elimina una película por su ID.
     * @param id El ID de la película a eliminar.
     */
    public void deleteFilm(Integer id) {
        filmRepository.deleteById(id);
    }

    /**
     * Busca películas por un fragmento de título (sin distinción entre mayúsculas y minúsculas).
     * @param title El fragmento de título a buscar.
     * @return Una lista de películas que coinciden con el título.
     */
    public Page<Film> findFilmsByTitle(String title, Pageable pageable) {
        return (Page<Film>) filmRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    /**
     * Busca películas por año de lanzamiento, con paginación.
     * @param year El año de lanzamiento.
     * @param pageable Objeto Pageable para la paginación.
     * @return Una página de películas lanzadas en el año especificado.
     */
    public Page<Film> findFilmsByReleaseYear(Year year, Pageable pageable) {
        return (Page<Film>) filmRepository.findByReleaseYear(year, pageable);
    }

    /**
     * Busca películas por clasificación MPAA, con paginación.
     * @param rating La clasificación MPAA.
     * @param pageable Objeto Pageable para la paginación.
     * @return Una página de películas con la clasificación especificada.
     */
    public Page<Film> findFilmsByRating(MpaaRating rating, Pageable pageable) {
        return (Page<Film>) filmRepository.findByRating(rating, pageable);
    }

    // Puedes añadir más métodos de lógica de negocio aquí
}

// Opcional: StringListConverter.java (Para manejar text[] en PostgreSQL)
// Necesitarías esta clase si JPA no puede mapear directamente List<String> a text[]
/*
package com.example.film.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = ","; // O un delimitador más complejo si los elementos pueden contener comas

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return null;
        }
        // Puedes ajustar la forma en que se serializa el array para PostgreSQL.
        // Una opción es unirse con comas y PostgreSQL puede manejarlo como un array si está entre llaves,
        // o si usas un tipo de dato como VARCHAR y luego lo parseas en la BD o en la app.
        // Para text[], lo ideal sería que Hibernate/EclipseLink tuvieran un mapeo nativo.
        // Si no, serializar a JSON es una alternativa común.
        // Para una simple coma como delimitador:
        return String.join(SPLIT_CHAR, stringList);
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        if (string == null || string.trim().isEmpty()) {
            return null;
        }
        // Deserializar el string de la base de datos de nuevo a List<String>
        return Arrays.stream(string.split(SPLIT_CHAR))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
*/
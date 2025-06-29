package com.example.application.services;

import com.example.application.data.Category;
import com.example.application.data.Film;
import com.example.application.data.Language;
import com.example.application.repositories.CategoryRepository;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Short id) {
        return categoryRepository.findById(id);
    }

}

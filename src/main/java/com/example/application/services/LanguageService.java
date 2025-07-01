package com.example.application.services;

import com.example.application.data.entity.Language;
import com.example.application.repositories.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Servicio para la entidad Language
@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    @Autowired
    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> findAll() {
        return languageRepository.findAll();
    }

    public Optional<Language> findById(Short id) {
        return languageRepository.findById(id);
    }

    // Puedes añadir más métodos de lógica de negocio para Language aquí
}



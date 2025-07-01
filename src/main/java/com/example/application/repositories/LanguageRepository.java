package com.example.application.repositories;

import com.example.application.data.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio para la entidad Language
@Repository
public interface LanguageRepository extends JpaRepository<Language, Short> {
    // Puedes añadir métodos de consulta personalizados aquí si los necesitas
}

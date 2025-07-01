// Language.java (Clase de entidad para la tabla 'language' - necesaria para la FK)
package com.example.application.data.entity;

import jakarta.persistence.*;

// Esta es una entidad de ejemplo para la tabla 'language'
// que es referenciada por la FK en la tabla 'film'.
// DEBES COMPLETAR ESTA CLASE SEGÚN EL DDL REAL DE TU TABLA 'language'.
@Entity
@Table(name = "language")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id")
    private Short languageId;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    // --- Constructores ---
    public Language() {
    }

    public Language(Short languageId, String name) {
        this.languageId = languageId;
        this.name = name;
    }

    // --- Getters y Setters ---

    public Short getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Short languageId) {
        this.languageId = languageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Language{" +
                "languageId=" + languageId +
                ", name='" + name + '\'' +
                '}';
    }
}


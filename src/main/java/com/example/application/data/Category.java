package com.example.application.data;

import jakarta.persistence.*;
// Language.java (Clase de entidad para la tabla 'category' - necesaria para la FK)


import jakarta.persistence.*;

// Esta es una entidad de ejemplo para la tabla 'category'
// que es referenciada por la FK en la tabla 'film'.

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Short categoryId;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    // --- Constructores ---
    public Category() {
    }

    public Category(Short categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    // --- Getters y Setters ---

    public Short getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Short categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                '}';
    }
}


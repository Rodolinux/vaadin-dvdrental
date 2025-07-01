package com.example.application.repositories;

import com.example.application.data.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// Repositorio para la entidad Category
@Repository
public interface CategoryRepository extends JpaRepository<Category, Short> {

}

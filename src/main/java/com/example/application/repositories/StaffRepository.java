package com.example.application.repositories;

import com.example.application.data.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositorio para la entidad Staff
@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

    // Consulta personalizada para cargar Staff junto con Address, City, Country y Store
    @Query("SELECT s FROM Staff s JOIN FETCH s.address a JOIN FETCH a.cityId c JOIN FETCH c.countryId JOIN FETCH s.store")
    Page<Staff> findAllWithAllRelations(Pageable pageable);

    @Query("SELECT s FROM Staff s JOIN FETCH s.address a JOIN FETCH a.cityId c JOIN FETCH c.countryId JOIN FETCH s.store WHERE s.staffId = :staffId")
    Optional<Staff> findByIdWithAllRelations(Integer staffId);

    // Búsqueda por nombre (case-insensitive)
    @Query("SELECT s FROM Staff s JOIN FETCH s.address a JOIN FETCH a.cityId c JOIN FETCH c.countryId JOIN FETCH s.store WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))")
    Page<Staff> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    // Búsqueda por apellido (case-insensitive)
    @Query("SELECT s FROM Staff s JOIN FETCH s.address a JOIN FETCH a.cityId c JOIN FETCH c.countryId JOIN FETCH s.store WHERE LOWER(s.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    Page<Staff> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
}

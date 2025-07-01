package com.example.application.repositories;

import com.example.application.data.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("select a from Address a join fetch a.cityId c join fetch c.countryId")
    Page<Address> findAllWithCityAndCountry(Pageable pageable);

    @Query("select a from Address a join fetch a.cityId c join fetch c.countryId where a.addressId = :addressId")
    Optional<Address> findByIdWithCityAndCountry(Integer addressId);

    // Búsqueda por parte de la dirección 1 (case-insensitive)
    @Query("SELECT a FROM Address a JOIN FETCH a.cityId c JOIN FETCH c.countryId WHERE LOWER(a.address1) LIKE LOWER(CONCAT('%', :address, '%'))")
    Page<Address> findByAddress1ContainingIgnoreCase(String address, Pageable pageable);

    // Búsqueda por parte del distrito (case-insensitive)
    @Query("SELECT a FROM Address a JOIN FETCH a.cityId c JOIN FETCH c.countryId WHERE LOWER(a.district) LIKE LOWER(CONCAT('%', :district, '%'))")
    Page<Address> findByDistrictContainingIgnoreCase(String district, Pageable pageable);
}

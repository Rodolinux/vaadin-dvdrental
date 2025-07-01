package com.example.application.data.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Clase de entidad para la tabla 'store' (simplificada para el ejemplo de Staff)
@Entity
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer storeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address; // Representa la dirección de la tienda

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;

    // --- Constructores ---
    public Store() {
    }

    public Store(Address address, LocalDateTime lastUpdate) {
        this.address = address;
        this.lastUpdate = lastUpdate;
    }

    // --- Getters y Setters ---
    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "Store ID: " + storeId;     }
}
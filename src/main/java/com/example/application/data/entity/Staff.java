package com.example.application.data.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Integer staffId;

    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER para cargar la dirección junto con el personal
    @JoinColumn(name = "address_id", nullable = false)
    private Address address; // Relación con la entidad Address

    @Column(name = "picture")
    private byte[] picture; // Puede ser nulo, tipo bytea en PostgreSQL

    @Column(name = "email", length = 50)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER para cargar la tienda junto con el personal
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; // Relación con la entidad Store

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "username", nullable = false, length = 16)
    private String username;

    @Column(name = "password", length = 40) // En una aplicación real, la contraseña estaría hasheada
    private String password;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;

    // --- Constructores ---
    public Staff() {
    }

    public Staff(String firstName, String lastName, Address address, Store store, Boolean active, String username, LocalDateTime lastUpdate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.store = store;
        this.active = active;
        this.username = username;
        this.lastUpdate = lastUpdate;
    }

    // --- Getters y Setters ---

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "staffId=" + staffId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address=" + (address != null ? address.getAddress1() : "null") +
                ", email='" + email + '\'' +
                ", store=" + (store != null ? store.getStoreId() : "null") +
                ", active=" + active +
                ", username='" + username + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}


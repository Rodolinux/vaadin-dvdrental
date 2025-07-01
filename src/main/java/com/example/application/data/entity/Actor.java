package com.example.application.data.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
@Table(name = "actor")
public class Actor //extends AbstractEntity
{
    @Id // Marca esta propiedad como la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O el tipo de generación de ID que uses en PostgreSQL
    @Column(name = "actor_id") // *
    private Integer actorId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "last_update")
    private LocalDate lastUpdate;

    public Integer getId() {return actorId;}
    public void setId() {this.actorId = actorId;}
    public Integer getActorId() {return actorId;};

    public void setActorId(Integer actorId) {
        this.actorId = actorId;
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
    public LocalDate getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}

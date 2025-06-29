package com.example.application.data;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id")
    private Short countryId;

    @Column(name="country", nullable = false, length =50 )
    private String countryName;

    @Column(name="last_update", nullable = false)
    private LocalDateTime lastUpdate;

    public Country() {
    }
    public Country(Short countryId, String countryName) {
        this.countryId = countryId;
        this.countryName = countryName;
    }
    public Short getCountryId() {
        return countryId;
    }
    public void setCountryId(Short countryId) {
        this.countryId = countryId;
    }
    public String getCountryName() {
        return countryName;
    }
    public void setCountry(String countryName) {
        this.countryName = countryName;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    @Override
    public String toString() {
        return "Country{"+
                "countryId" + countryId +
                ", country" + countryName + '\'' +
                '}';
    }
}

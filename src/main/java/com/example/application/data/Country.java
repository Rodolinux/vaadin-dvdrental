package com.example.application.data;
import jakarta.persistence.*;
@Entity
@Table(name = "country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id")
    private Short countryId;

    @Column(name="country", nullable = false, length =50 )
    private String country;

    public Country() {
    }
    public Country(Short countryId, String country) {
        this.countryId = countryId;
        this.country = country;
    }
    public Short getCountryId() {
        return countryId;
    }
    public void setCountryId(Short countryId) {
        this.countryId = countryId;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Country{"+
                "countryId" + countryId +
                ", country" + country + '\'' +
                '}';
    }
}

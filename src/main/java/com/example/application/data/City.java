package com.example.application.data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="city_id")
    private Integer cityId;

    @Column(name="city", nullable=false, length = 50)
    private String cityName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="country_id", nullable=false)
    private Country countryId;

    @Column(name="last_update", nullable = false)
    private LocalDateTime lastUpdate;

    public City() {}

    public City(String cityName, Country countryId, LocalDateTime lastUpdate) {
        this.cityName = cityName;
        this.countryId = countryId;
        this.lastUpdate = lastUpdate;
    }

    public Integer getCityId(){
        return cityId;
    }
    public void setCityId(Integer cityId){
        this.cityId = cityId;
    }
    public String getCityName(){
        return cityName;
    }
    public void setCityName(String cityName){
        this.cityName = cityName;
    }
    public Country getCountry(){
        return countryId;
    }
    public void setCountry(Country countryId){
        this.countryId = countryId;
    }
    public LocalDateTime getLastUpdate(){
        return lastUpdate;
    }
    public void setLastUpdate(LocalDateTime lastUpdate){
        this.lastUpdate = lastUpdate;
    }
    @Override
    public String toString() {
        return "City{" +
                "cityId=" + cityId +
                ", cityName='" + cityName + '\'' +
                ", country=" + (countryId != null ? countryId.getCountryName() : "null") +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}

package com.example.application.data.reportbean;

import com.example.application.data.entity.City;

import java.time.LocalDateTime;

public class CityReportBean {
    private Integer cityId;
    private String cityName;
    private String countryName;
    private LocalDateTime lastUpdate;

    public CityReportBean(City city) {
        this.cityId = city.getCityId();
        this.cityName = city.getCityName();
        this.countryName = city.getCityName() != null ? city.getCountry().getCountryName() : "N/A";
        this.lastUpdate = city.getLastUpdate();
    }

    public Integer getCityId() {
        return cityId;
    }
    public String getCityName() {
        return cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
}

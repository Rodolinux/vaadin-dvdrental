package com.example.application.data.reportbean;

import com.example.application.data.entity.Film;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FilmReportBean {
    private Integer filmId;
    private String title;
    private String description;
    private Integer releaseYear;
    private String languageName;
    private Short rentalDuration;
    private BigDecimal rentalRate;
    private Short length;
    private BigDecimal replacementCost;
    private String rating;
    private LocalDateTime lastUpdate;
    private String specialFeatures; // Convertido a String para el reporte

    public FilmReportBean(Film film) {
        this.filmId = film.getFilmId();
        this.title = film.getTitle();
        this.description = film.getDescription();
        this.releaseYear = film.getReleaseYear() != null ? film.getReleaseYear().getValue() : null;
        this.languageName = film.getLanguage() != null ? film.getLanguage().getName() : "N/A";
        this.rentalDuration = film.getRentalDuration();
        this.rentalRate = film.getRentalRate();
        this.length = film.getLength();
        this.replacementCost = film.getReplacementCost();
        this.rating = film.getRating() != null ? film.getRating().name().replace("_", "-") : null;
        this.lastUpdate = film.getLastUpdate();
        // Unir la lista de specialFeatures en una sola cadena para el reporte
        this.specialFeatures = film.getSpecialFeatures() != null && !film.getSpecialFeatures().isEmpty() ?
                String.join(", ", film.getSpecialFeatures()) : "";
    }

    // Getters para todas las propiedades (JasperReports las necesita)
    public Integer getFilmId() {
        return filmId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public String getLanguageName() {
        return languageName;
    }

    public Short getRentalDuration() {
        return rentalDuration;
    }

    public BigDecimal getRentalRate() {
        return rentalRate;
    }

    public Short getLength() {
        return length;
    }

    public BigDecimal getReplacementCost() {
        return replacementCost;
    }

    public String getRating() {
        return rating;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public String getSpecialFeatures() {
        return specialFeatures;
    }
}
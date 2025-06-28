package com.example.application.data;
import com.example.application.converters.MpaaRatingConverter;
import com.example.application.converters.StringListConverter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;


// Clase de entidad para la tabla 'film'
@Entity
@Table(name = "film")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Asume que la secuencia de PostgreSQL genera IDs
    @Column(name = "film_id")
    private Integer filmId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Para el tipo 'year' de PostgreSQL, se mapea a java.time.Year
    // Dependiendo de la configuración de JPA, podría requerir un conversor
    // o anotaciones específicas para manejarlo como un Short/Integer.
    // java.time.Year es semánticamente más correcto.
    @Column(name = "release_year")
    private Year releaseYear;

    // Relación Many-to-One con la entidad Language
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", nullable = false)
    private Language language; // Esto mapea a language_id

    @Column(name = "rental_duration", nullable = false)
    private Short rentalDuration;

    @Column(name = "rental_rate", nullable = false, precision = 4, scale = 2)
    private BigDecimal rentalRate;

    @Column(name = "length")
    private Short length;

    @Column(name = "replacement_cost", nullable = false, precision = 5, scale = 2)
    private BigDecimal replacementCost;

    // Usar el @Convert con el MpaaRatingConverter para manejar la traducción
    @Convert(converter = MpaaRatingConverter.class)
    @Column(name = "rating", columnDefinition = "mpaa_rating")
    private MpaaRating rating;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;

    @Convert(converter = StringListConverter.class)
    @Column(name = "special_features", columnDefinition = "text[]")
    private List<String> specialFeatures;

    // fulltext (tsvector) se omite de la entidad ya que es un tipo de búsqueda
    // y generalmente se maneja a través de consultas nativas o librerías específicas.

    // --- Constructores ---
    public Film() {
    }

    // Constructor con campos esenciales (puedes añadir más según necesites)
    public Film(String title, Language language, Short rentalDuration, BigDecimal rentalRate, LocalDateTime lastUpdate) {
        this.title = title;
        this.language = language;
        this.rentalDuration = rentalDuration;
        this.rentalRate = rentalRate;
        this.lastUpdate = lastUpdate;
    }

    // --- Getters y Setters ---

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Year getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Year releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Short getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(Short rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public BigDecimal getRentalRate() {
        return rentalRate;
    }

    public void setRentalRate(BigDecimal rentalRate) {
        this.rentalRate = rentalRate;
    }

    public Short getLength() {
        return length;
    }

    public void setLength(Short length) {
        this.length = length;
    }

    public BigDecimal getReplacementCost() {
        return replacementCost;
    }

    public void setReplacementCost(BigDecimal replacementCost) {
        this.replacementCost = replacementCost;
    }

    public MpaaRating getRating() {
        return rating;
    }

    public void setRating(MpaaRating rating) {
        this.rating = rating;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<String> getSpecialFeatures() {
        return specialFeatures;
    }

    public void setSpecialFeatures(List<String> specialFeatures) {
        this.specialFeatures = specialFeatures;
    }

    @Override
    public String toString() {
        return "Film{" +
                "filmId=" + filmId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", releaseYear=" + releaseYear +
                ", language=" + (language != null ? language.getName() : "null") + // Evita recursión infinita
                ", rentalDuration=" + rentalDuration +
                ", rentalRate=" + rentalRate +
                ", length=" + length +
                ", replacementCost=" + replacementCost +
                ", rating=" + rating +
                ", lastUpdate=" + lastUpdate +
                ", specialFeatures=" + specialFeatures +
                '}';
    }
}

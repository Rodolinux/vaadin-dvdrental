package com.example.application.data;

import com.example.application.data.MpaaRating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// Este conversor maneja la traducción entre el String de la DB y el enum de Java.
@Converter(autoApply = true) // autoApply = true hace que JPA use este conversor automáticamente para MpaaRating
public class MpaaRatingConverter implements AttributeConverter<MpaaRating, String> {

    @Override
    public String convertToDatabaseColumn(MpaaRating mpaaRating) {
        if (mpaaRating == null) {
            return null;
        }
        // Cuando se guarda en la base de datos, convertimos NC_17 a NC-17
        return mpaaRating.name().replace("_", "-");
    }

    @Override
    public MpaaRating convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        // Cuando se lee de la base de datos, convertimos NC-17 a NC_17
        return MpaaRating.valueOf(dbData.replace("-", "_"));
    }
}
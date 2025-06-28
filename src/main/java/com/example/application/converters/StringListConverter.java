package com.example.application.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter(autoApply = true) // autoApply = true para que JPA lo aplique automáticamente
public class StringListConverter implements AttributeConverter<List<String>, String> {

    // Utiliza un delimitador que no sea probable que aparezca en los datos reales.
    // O considera usar una librería para JSONB o tipos nativos de PostgreSQL.
    // Para el tipo text[] de PostgreSQL, a menudo se formatea como '{item1,item2,item3}'
    // Para simplificar, aquí usaremos una coma. Para un mapeo 100% fiel a text[],
    // se requeriría una implementación más avanzada o una librería como hibernate-types.
    private static final String LIST_DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return null;
        }
        // Convierte la lista a una cadena separada por el delimitador
        // Para PostgreSQL text[], a menudo querrías '{' + String.join(",", stringList) + '}'
        // Pero JPA/Hibernate a veces maneja la serialización del array si el tipo de columna es correcto.
        // Si no funciona directamente, se podría necesitar una solución más compleja (ej. JSONB o hibernate-types)
        return String.join(LIST_DELIMITER, stringList);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        // Convierte la cadena separada por el delimitador a una lista
        return Arrays.stream(s.split(LIST_DELIMITER))
                .map(String::trim)
                .filter(item -> !item.isEmpty()) // Elimina posibles cadenas vacías después del trim
                .collect(Collectors.toList());
    }
}
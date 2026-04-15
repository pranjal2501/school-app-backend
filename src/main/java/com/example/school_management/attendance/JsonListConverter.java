package com.example.school_management.attendance;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

@Converter
public class JsonListConverter implements AttributeConverter<List<Integer>, String> {

    private final static ObjectMapper mapper = new ObjectMapper();

    // Convert List<Integer> to JSON String (Writing to DB)
    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        try {
            return attribute == null ? "[]" : mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON writing error", e);
        }
    }

    // Convert JSON String to List<Integer> (Reading from DB)
    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) return new ArrayList<>();
            return mapper.readValue(dbData, new TypeReference<List<Integer>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON reading error", e);
        }
    }
}
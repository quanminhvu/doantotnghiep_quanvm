package com.quanvm.applyin.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter
public class ListToJsonStringConverter implements AttributeConverter<List<String>, String> {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    try {
      if (attribute == null) return null;
      return mapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to write list as JSONN", e);
    }
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    try {
      if (dbData == null || dbData.isEmpty()) return Collections.emptyList();
      return mapper.readValue(dbData, new TypeReference<List<String>>() {});
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to read list from JSON", e);
    }
  }
}



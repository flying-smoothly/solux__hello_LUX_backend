package com.eum.hello_lux_quiz.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class VoiceSettingConverter implements AttributeConverter<VoiceSetting, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(VoiceSetting attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("VoiceSetting을 JSON으로 변환하는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public VoiceSetting convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new VoiceSetting(); // 기본값 객체 반환
        }
        try {
            return objectMapper.readValue(dbData, VoiceSetting.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON을 VoiceSetting으로 변환하는 중 오류가 발생했습니다.", e);
        }
    }
}

package com.eum.hello_lux_quiz.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
            JsonNode node = objectMapper.readTree(dbData);
            // H2의 json 컬럼 타입과 String 기반 컨버터 조합에서, 값이 JSON 객체가 아니라
            // 그 객체를 다시 문자열로 감싼(이스케이프된) 형태로 내려오는 경우가 있다.
            // 문자열 노드로 파싱됐다면 한 번 더 풀어서 실제 객체를 얻는다.
            if (node.isTextual()) {
                node = objectMapper.readTree(node.asText());
            }
            return objectMapper.treeToValue(node, VoiceSetting.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON을 VoiceSetting으로 변환하는 중 오류가 발생했습니다.", e);
        }
    }
}

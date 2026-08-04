package com.eum.hello_lux_quiz.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.eum.hello_lux_quiz.domain.DetailEvent;
import com.eum.hello_lux_quiz.domain.LifeDb;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LifeDbResponseDto {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String title;
    private String category;

    @JsonProperty("record_date")
    private String recordDate;

    // String -> List<FamilyDto> 변경
    private List<FamilyDto> family;
    
    private String hometown;
    private String job;
    private String place;

    @JsonProperty("like")
    private String like;

    private List<LifeDbEventDto> events;


    public LifeDbResponseDto(LifeDb entity, List<DetailEvent> detailEvents) {
        this.title = entity.getTitle();
        this.category = "기본정보";
        this.recordDate = (entity.getRecordDate() != null) ? entity.getRecordDate().toString() : null;
        
        // DB의 JSON 문자열(TEXT)을 List<FamilyDto> 객체로 파싱
        this.family = parseFamilyJson(entity.getFamily());
        
        this.hometown = entity.getHometown();
        this.job = entity.getJob();
        this.place = entity.getPlace();
        this.like = entity.getLikes();

        if (detailEvents != null) {
            this.events = detailEvents.stream()
                    .map(LifeDbEventDto::new)
                    .collect(Collectors.toList());
        }
    }

    // JSON 문자열 -> List<FamilyDto> 변환 
    private List<FamilyDto> parseFamilyJson(String familyJson) {
        if (familyJson == null || familyJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(familyJson, new TypeReference<List<FamilyDto>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }
}

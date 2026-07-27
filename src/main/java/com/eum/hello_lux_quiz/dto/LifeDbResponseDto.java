package com.eum.hello_lux_quiz.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.eum.hello_lux_quiz.domain.DetailEvent;
import com.eum.hello_lux_quiz.domain.LifeDb;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LifeDbResponseDto {

    private String title;
    private String category;

    @JsonProperty("record_date")
    private String recordDate;

    private String family;
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
        this.family = entity.getFamily();
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
}

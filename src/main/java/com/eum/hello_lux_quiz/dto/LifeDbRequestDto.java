package com.eum.hello_lux_quiz.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LifeDbRequestDto {

    private String title;

    @JsonProperty("record_date")
    private String recordDate;

    //  List<FamilyDto> family로 변경
    private List<FamilyDto> family;
    
    private String hometown;
    private String job;
    private String place;

    @JsonProperty("like")
    private String like;

    private String event;

    @JsonProperty("photo_url")
    private String photoUrl;

    private String category;

    // 수동 생성자
    public LifeDbRequestDto(String title, String recordDate, List<FamilyDto> family, 
                            String hometown, String job, String place, String like, 
                            String event, String photoUrl, String category) {
        this.title = title;
        this.recordDate = recordDate;
        this.family = family;
        this.hometown = hometown;
        this.job = job;
        this.place = place;
        this.like = like;
        this.event = event;
        this.photoUrl = photoUrl;
        this.category = category;
    }
}

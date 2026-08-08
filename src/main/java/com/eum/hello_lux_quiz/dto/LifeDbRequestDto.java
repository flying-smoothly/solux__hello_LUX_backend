package com.eum.hello_lux_quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LifeDbRequestDto {

    private String title;

    @JsonProperty("record_date")
    private String recordDate;

    private String family;
    private String hometown;
    private String job;
    private String place;

    @JsonProperty("like")
    private String like;

    private String event;

    @JsonProperty("photo_url")
    private String photoUrl;

    private String category;
}


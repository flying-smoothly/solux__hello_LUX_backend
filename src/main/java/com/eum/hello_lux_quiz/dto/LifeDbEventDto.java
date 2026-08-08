package com.eum.hello_lux_quiz.dto;

import com.eum.hello_lux_quiz.domain.DetailEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LifeDbEventDto {

    private String event;

    @JsonProperty("photo_url")
    private String photoUrl;

    private String category;

    public LifeDbEventDto(DetailEvent entity) {
        this.event = entity.getEvent();      // DetailEvent의 event
        this.photoUrl = entity.getPhotoUrl(); // DetailEvent의 photo_url
        this.category = entity.getCategory(); // DetailEvent의 category
    }
}

package com.eum.hello_lux_quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LifeDbEventRequestDto {

    private String event;

    @JsonProperty("photo_url")
    private String photoUrl;

    private String category;
}

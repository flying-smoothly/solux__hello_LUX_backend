package com.eum.hello_lux_quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LifeDbUpdateRequestDto {

    @JsonProperty("memory_id")
    private Integer memoryId;

    private String family;
    private String hometown;
    private String job;
    private String place;

    @JsonProperty("like")
    private String like;
}

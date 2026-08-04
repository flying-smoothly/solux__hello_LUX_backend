package com.eum.hello_lux_quiz.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LifeDbUpdateRequestDto {

    @JsonProperty("memory_id")
    private Integer memoryId;

    //> List<FamilyDto> 변경
    private List<FamilyDto> family;
    
    private String hometown;
    private String job;
    private String place;

    @JsonProperty("like")
    private String like;

    // 수동 생성자
    public LifeDbUpdateRequestDto(Integer memoryId, List<FamilyDto> family, String hometown, String job, String place, String like) {
        this.memoryId = memoryId;
        this.family = family;
        this.hometown = hometown;
        this.job = job;
        this.place = place;
        this.like = like;
    }
}

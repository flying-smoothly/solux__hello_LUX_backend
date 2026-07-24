package com.eum.hello_lux_quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DoctorLevelUpdateRequest {

    @JsonProperty("quiz_type")
    private String quizType;

    private Integer level;

    // Getter (Lombok 미동작 대비)
    public String getQuizType() {
        return quizType;
    }

    public Integer getLevel() {
        return level;
    }
}

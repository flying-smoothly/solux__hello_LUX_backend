package com.eum.hello_lux_quiz.dto;

import com.eum.hello_lux_quiz.domain.QuizResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class QuizResultResponse {

    private LocalDate date;

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("correct_count")
    private Integer correctCount;

    private Integer hint;

    @JsonProperty("calculate")
    private String calculate;

    @JsonProperty("total_score")
    private Integer totalScore;

    public static QuizResultResponse from(QuizResult entity, Integer totalScore) {
        return QuizResultResponse.builder()
                .date(entity.getResultDate())
                .totalCount(entity.getTotalCount())
                .correctCount(entity.getCorrectCount())
                .hint(entity.getHint())
                .calculate(entity.getCaculate())
                .totalScore(totalScore)
                .build();
    }
}

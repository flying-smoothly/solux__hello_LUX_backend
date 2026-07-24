package com.eum.hello_lux_quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuizFeedbackResponse {

    @JsonProperty("feedback_id")
    private Integer feedbackId;

    @JsonProperty("set_id")
    private Integer setId;

    @JsonProperty("feedback_content")
    private String feedbackContent;

    @JsonProperty("created_at")
    private String createdAt;

    @Builder
    public QuizFeedbackResponse(Integer feedbackId, Integer setId, String feedbackContent, String createdAt) {
        this.feedbackId = feedbackId;
        this.setId = setId;
        this.feedbackContent = feedbackContent;
        this.createdAt = createdAt;
    }
}

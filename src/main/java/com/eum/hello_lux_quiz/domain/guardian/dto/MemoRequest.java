package com.eum.hello_lux_quiz.domain.guardian.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record MemoRequest(
        @JsonProperty("record_date") LocalDate recordDate,
        @JsonProperty("health_status") String healthStatus,
        @JsonProperty("sleep_status") String sleepStatus,
        @JsonProperty("meal_status") String mealStatus,
        @JsonProperty("pain_status") String painStatus,
        @JsonProperty("mood_status") String moodStatus,
        List<String> behaviors,
        @JsonProperty("need_referral") Boolean needReferral,        
        String content
) {
}

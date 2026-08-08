package com.eum.hello_lux_quiz.domain.guardian.dto;

import com.eum.hello_lux_quiz.domain.guardian.entity.StatusMemo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 상태 기록 목록/상세 항목. 저장된 감정·행동(콤마 문자열)은 리스트로 복원해 전달한다.
 */
public record MemoResponse(
        @JsonProperty("memo_id") Long memoId,
        @JsonProperty("record_date") LocalDate recordDate,
        @JsonProperty("health_status") String healthStatus,
        @JsonProperty("sleep_status") String sleepStatus,
        @JsonProperty("meal_status") String mealStatus,
        @JsonProperty("pain_status") String painStatus,
        @JsonProperty("mood_status") String moodStatus,
        List<String> behaviors,
        @JsonProperty("need_referral") boolean needReferral,
        String content,
        @JsonProperty("created_at") LocalDate createdAt
) {
    public static MemoResponse from(StatusMemo memo) {
        return new MemoResponse(
                memo.getMemoId(),
                memo.getRecordDate(),
                memo.getHealthStatus(),
                memo.getSleepStatus(),
                memo.getMealStatus(),
                memo.getPainStatus(),
                memo.getMoodStatus(),
                toList(memo.getBehaviors()),
                memo.isNeedReferral(),
                memo.getContent(),
                memo.getCreatedAt()
        );
    }

    private static List<String> toList(String joined) {
        if (joined == null || joined.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(joined.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
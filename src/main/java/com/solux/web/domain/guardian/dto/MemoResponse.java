package com.solux.web.domain.guardian.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.solux.web.domain.guardian.entity.StatusMemo;

import java.time.LocalDate;

/**
 * 상태 메모 목록 항목.
 * 예: {"memo_id":5,"content":"오늘 기분 좋아 보였음","created_at":"2026-05-29"}
 */
public record MemoResponse(
        @JsonProperty("memo_id") Long memoId,
        String content,
        @JsonProperty("created_at") LocalDate createdAt
) {
    public static MemoResponse from(StatusMemo memo) {
        return new MemoResponse(memo.getMemoId(), memo.getContent(), memo.getCreatedAt());
    }
}

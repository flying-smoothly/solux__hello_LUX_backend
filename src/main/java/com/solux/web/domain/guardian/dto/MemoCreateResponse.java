package com.solux.web.domain.guardian.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.solux.web.domain.guardian.entity.StatusMemo;

import java.time.LocalDate;

/**
 * 상태 메모 작성 응답.
 * 예: {"memo_id":5,"created_at":"2026-05-29"}
 */
public record MemoCreateResponse(
        @JsonProperty("memo_id") Long memoId,
        @JsonProperty("created_at") LocalDate createdAt
) {
    public static MemoCreateResponse from(StatusMemo memo) {
        return new MemoCreateResponse(memo.getMemoId(), memo.getCreatedAt());
    }
}

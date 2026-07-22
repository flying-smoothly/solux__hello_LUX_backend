package com.solux.web.domain.guardian.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 환자 상태 메모 작성 요청.
 * 예: {"content":"오늘 기분 좋아 보였음"}
 */
public record MemoRequest(
        @NotBlank(message = "메모 내용은 필수입니다.")
        String content
) {
}

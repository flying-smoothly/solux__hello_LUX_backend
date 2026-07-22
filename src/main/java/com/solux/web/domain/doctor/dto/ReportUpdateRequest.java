package com.solux.web.domain.doctor.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 진료 리포트 작성/수정 요청.
 * 예: {"report":"인지 기능 전반적으로 양호"}
 */
public record ReportUpdateRequest(
        @NotBlank(message = "리포트 내용은 필수입니다.")
        String report
) {
}

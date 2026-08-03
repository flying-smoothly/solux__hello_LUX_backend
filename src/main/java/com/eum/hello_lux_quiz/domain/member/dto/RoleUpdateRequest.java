package com.eum.hello_lux_quiz.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 역할 설정 요청. 회원가입 이후 역할선택 화면에서 호출한다.
 * 예: {"role":"patient"}
 */
public record RoleUpdateRequest(
        @NotBlank(message = "역할(role)은 필수입니다.")
        String role
) {
}
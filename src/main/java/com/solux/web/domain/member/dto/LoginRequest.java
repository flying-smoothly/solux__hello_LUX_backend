package com.solux.web.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청.
 * 예: {"user_email":"user@email.com","user_pw":"1234"}
 */
public record LoginRequest(
        @JsonProperty("user_email")
        @NotBlank(message = "이메일은 필수입니다.")
        String userEmail,

        @JsonProperty("user_pw")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String userPw
) {
}

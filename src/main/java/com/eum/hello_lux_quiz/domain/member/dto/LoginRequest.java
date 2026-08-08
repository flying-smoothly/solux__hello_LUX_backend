package com.eum.hello_lux_quiz.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청.
 * 예: {"user_id":"hong1234","user_pw":"12345678"}
 *  */
public record LoginRequest(
        @JsonProperty("user_id")
        @NotBlank(message = "아이디는 필수입니다.")
        String userId,

        @JsonProperty("user_pw")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String userPw
) {
}

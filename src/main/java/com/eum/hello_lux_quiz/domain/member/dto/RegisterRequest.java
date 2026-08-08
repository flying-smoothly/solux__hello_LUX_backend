package com.eum.hello_lux_quiz.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
+ * 회원가입 요청. 아이디 기반 가입(이메일 인증 미사용).
 * 역할(role)은 가입 이후 별도 API(PATCH /api/auth/role)로 설정하므로 여기서 받지 않는다.
+ * 필드: 아이디 · 비밀번호 · 이름 · 생년월일 (전화번호는 마이페이지에서 선택 입력)
+ * 예: {"user_id":"hong1234","user_pw":"12345678","name":"홍길동","birth_date":"1950-01-01"}
 */
public record RegisterRequest(
        @JsonProperty("user_id")
        @NotBlank(message = "아이디는 필수입니다.")
        @Pattern(regexp = "^[A-Za-z0-9]{4,20}$", message = "아이디는 영문·숫자 4~20자여야 합니다.")
        String userId,

        @JsonProperty("user_pw")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String userPw,

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @JsonProperty("birth_date")
        @NotNull(message = "생년월일은 필수입니다.")
        LocalDate birthDate
) {
}

package com.solux.web.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * 회원가입 요청.
 * 예: {"user_email":"user@email.com","user_pw":"1234","role":"patient","name":"홍길동","birth_date":"1950-01-01"}
 */
public record RegisterRequest(
        @JsonProperty("user_email")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String userEmail,

        @JsonProperty("user_pw")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String userPw,

        @NotBlank(message = "역할(role)은 필수입니다.")
        String role,

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @JsonProperty("birth_date")
        @NotNull(message = "생년월일은 필수입니다.")
        LocalDate birthDate
) {
}

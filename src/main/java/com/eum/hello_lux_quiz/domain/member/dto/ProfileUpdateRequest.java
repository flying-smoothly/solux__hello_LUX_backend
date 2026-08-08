package com.eum.hello_lux_quiz.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * 개인정보 수정 요청.
 * current_pw(현재 비밀번호)는 본인 확인을 위해 필수이며, user_pw(새 비밀번호)는 선택이다.
 * 예: {"name":"홍길동","birth_date":"1950-01-01","current_pw":"현재비밀번호","user_pw":"새비밀번호"}
 */
public record ProfileUpdateRequest(
        String name,

        @JsonProperty("birth_date")
        LocalDate birthDate,

        @JsonProperty("current_pw")
        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        String currentPw,

        @JsonProperty("user_pw")
        String userPw
) {
}

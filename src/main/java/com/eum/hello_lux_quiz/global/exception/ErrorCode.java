package com.eum.hello_lux_quiz.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 회원/인증
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "지원하지 않는 역할입니다."),

    // 환자
    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 환자입니다."),
    PATIENT_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 등록된 환자 정보입니다."),

    // 연동
    ALREADY_LINKED(HttpStatus.CONFLICT, "이미 연동된 환자입니다."),
    LINK_NOT_FOUND(HttpStatus.NOT_FOUND, "연동 정보가 존재하지 않습니다."),

    // 메모
    MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메모입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

package com.eum.hello_lux_quiz.global.exception;

import lombok.Getter;

/**
 * 에러 응답. 모든 에러는 {"message":"..."} 형태로 통일한다.
 * (HTTP 상태 코드는 응답 상태에 담기므로 본문에는 메시지만 포함한다.)
 */

@Getter
public class ErrorResponse {
    private final String message;

    private ErrorResponse(String message) {
        this.message = message;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getMessage());
    }

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }
}

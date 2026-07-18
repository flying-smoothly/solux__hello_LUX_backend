package com.solux.web.global.common;

/**
 * 단순 메시지 응답. 예: {"message":"수정 완료"}
 */
public record MessageResponse(String message) {
    public static MessageResponse of(String message) {
        return new MessageResponse(message);
    }
}

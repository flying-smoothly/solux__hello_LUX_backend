package com.eum.hello_lux_quiz.global.common;

import com.eum.hello_lux_quiz.global.exception.CustomException;
import com.eum.hello_lux_quiz.global.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 현재 인증된 사용자의 정보를 SecurityContext 에서 꺼내는 헬퍼.
 */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return authentication.getName();
    }
}

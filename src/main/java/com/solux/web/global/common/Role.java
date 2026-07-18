package com.solux.web.global.common;

/**
 * 회원 역할. API 명세서의 role 필드 값("patient", "guardian", "doctor")과 매핑된다.
 */
public enum Role {
    PATIENT("patient"),
    GUARDIAN("guardian"),
    DOCTOR("doctor");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role from(String value) {
        for (Role role : values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 role 입니다: " + value);
    }
}

package com.solux.web.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.solux.web.domain.patient.entity.PatientProfile;

/**
 * 환자 정보 조회 응답.
 * 예: {"p_code":1001,"name":"홍길동","diagnosis":"경도인지장애","personality":"온화함"}
 */
public record PatientInfoResponse(
        @JsonProperty("p_code") Integer pCode,
        String name,
        String diagnosis,
        String personality
) {
    public static PatientInfoResponse from(PatientProfile profile) {
        return new PatientInfoResponse(
                profile.getPCode(),
                profile.getName(),
                profile.getDiagnosis(),
                profile.getPersonality()
        );
    }
}

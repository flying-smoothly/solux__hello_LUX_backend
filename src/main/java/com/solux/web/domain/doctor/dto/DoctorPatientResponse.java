package com.solux.web.domain.doctor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.solux.web.domain.patient.entity.PatientProfile;

/**
 * 담당 환자 목록 항목.
 * 예: {"p_code":1001,"name":"홍길동","diagnosis":"경도인지장애"}
 */
public record DoctorPatientResponse(
        @JsonProperty("p_code") Integer pCode,
        String name,
        String diagnosis
) {
    public static DoctorPatientResponse from(PatientProfile profile) {
        return new DoctorPatientResponse(profile.getPCode(), profile.getName(), profile.getDiagnosis());
    }
}

package com.eum.hello_lux_quiz.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.eum.hello_lux_quiz.domain.patient.entity.Patient;

/**
 * 로그인한 환자 본인의 정보 조회 응답 (GET /api/patient/me).
 *
 * <p>등록 응답에서 한 번만 받을 수 있던 내부 식별자(internal_code)를 다시 조회하기 위한 API 다.
 * 다른 기기·브라우저로 재로그인해도 이 API 로 두 코드를 모두 얻을 수 있다.</p>
 *
 * <p>예:
 * {"internal_code":1001,"p_code":"AB37X2","name":"홍길동","gender":"남",
 *  "diagnosis":"경도인지장애","personality":"온화함","speech_style":null,
 *  "cognitive_support_level":"보통","guardian_companion":true,"patient_status":"유지"}</p>
 */
public record PatientMeResponse(
        /** 내부 식별자. 등록 시 발급되는 숫자 코드. */
        @JsonProperty("internal_code") Integer internalCode,

        /** 보호자·의사 연동용 6자리 코드. */
        @JsonProperty("p_code") String pCode,

        String name,
        String gender,
        String diagnosis,
        String personality,

        @JsonProperty("speech_style") String speechStyle,

        @JsonProperty("cognitive_support_level") String cognitiveSupportLevel,

        @JsonProperty("guardian_companion") Boolean guardianCompanion,

        @JsonProperty("patient_status") String patientStatus
) {
    public static PatientMeResponse from(Patient patient, String name) {
        return new PatientMeResponse(
               patient.getPCode(),
                patient.getPatientCode(),
                name,
                patient.getGender(),
                patient.getDiagnosis(),
                patient.getPersonality(),
                patient.getSpeechStyle(),
                patient.getCognitiveSupportLevel(),
                patient.getGuardianCompanion(),
                patient.getPatientStatus()
        );
    }
}
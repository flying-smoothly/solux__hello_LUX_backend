package com.eum.hello_lux_quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DoctorLevelUpdateRequest {

    // 명세서 Request Body Key인 "paitient_status"와 매핑
    @JsonProperty("paitient_status")
    private String patientStatus;

    public String getPatientStatus() {
        return patientStatus;
    }
}

package com.eum.hello_lux_quiz.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DoctorLevelUpdateResponse {

    private String message;

    public DoctorLevelUpdateResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}




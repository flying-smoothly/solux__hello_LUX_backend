package com.eum.hello_lux_quiz.controller;

import com.eum.hello_lux_quiz.domain.VoiceSetting;
import com.eum.hello_lux_quiz.dto.PatientStatusUpdateRequest;
import com.eum.hello_lux_quiz.dto.VoiceSettingRequestDto;
import com.eum.hello_lux_quiz.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientProfileController {

    private final PatientService patientService;
    
    /**
     * 환자 상태, 인지 지원 수준, 보호자 동행 여부 저장/수정 API
     */
    @PutMapping("/{pCode}/status")
    public ResponseEntity<Void> updatePatientStatus(
            @PathVariable("pCode") Integer pCode,
            @RequestBody PatientStatusUpdateRequest requestDto) {
        patientService.updatePatientStatus(pCode, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 음성 설정 저장/수정 API
     */
    @PutMapping("/{pCode}/voice-setting")
    public ResponseEntity<VoiceSetting> updateVoiceSetting(
            @PathVariable("pCode") Integer pCode,
            @RequestBody VoiceSettingRequestDto requestDto) {
        VoiceSetting updatedSetting = patientService.updateVoiceSetting(pCode, requestDto);
        return ResponseEntity.ok(updatedSetting);
    }
}

package com.eum.hello_lux_quiz.controller;

import com.eum.hello_lux_quiz.domain.VoiceSetting;
import com.eum.hello_lux_quiz.dto.VoiceSettingRequestDto;
import com.eum.hello_lux_quiz.service.PatientProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientProfileController {

    private final PatientProfileService patientProfileService;

    /**
     * 음성 설정 저장/수정 API
     */
    @PutMapping("/{pCode}/voice-setting")
    public ResponseEntity<VoiceSetting> updateVoiceSetting(
            @PathVariable("pCode") Integer pCode,
            @RequestBody VoiceSettingRequestDto requestDto) {

        VoiceSetting updatedSetting = patientProfileService.updateVoiceSetting(pCode, requestDto);
        return ResponseEntity.ok(updatedSetting);
    }
}

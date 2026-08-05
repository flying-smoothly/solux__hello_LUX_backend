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
    // 연동용 코드(p_code, 예: "AB37X2")를 내부 식별자로 변환하기 위해 사용한다.
    private final com.eum.hello_lux_quiz.domain.patient.service.PatientService patientLookupService;
    
    /**
     * 환자 상태, 인지 지원 수준, 보호자 동행 여부 저장/수정 API
     */
    @PutMapping("/{p_code}/status")
    public ResponseEntity<Void> updatePatientStatus(
            @PathVariable("p_code") String patientCode,
            @RequestBody PatientStatusUpdateRequest requestDto) {
        Integer pCode = patientLookupService.resolvePCode(patientCode);
        patientService.updatePatientStatus(pCode, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 음성 설정 저장/수정 API
     */
    @PutMapping("/{p_code}/voice-setting")
    public ResponseEntity<VoiceSetting> updateVoiceSetting(
            @PathVariable("p_code") String patientCode,
            @RequestBody VoiceSettingRequestDto requestDto) {
        Integer pCode = patientLookupService.resolvePCode(patientCode);
        VoiceSetting updatedSetting = patientService.updateVoiceSetting(pCode, requestDto);
        return ResponseEntity.ok(updatedSetting);
    }
}

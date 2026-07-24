package com.eum.hello_lux_quiz.controller;

import com.eum.hello_lux_quiz.dto.PatientStatusUpdateRequest;
import com.eum.hello_lux_quiz.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final PatientService patientService;

    public DoctorController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * 의사가 특정 환자의 인지 상태 및 퀴즈 난이도 단계(유지/주의/위험)를 조절하는 API PUT
     * /api/doctor/{p_code}/status
     */
    @PutMapping("/{p_code}/status")
    public ResponseEntity<String> updatePatientStatus(
            @PathVariable("p_code") Integer pCode,
            @RequestBody PatientStatusUpdateRequest request) {

        patientService.updatePatientStatus(pCode, request.getPatientStatus());

        return ResponseEntity.ok("환자 상태 및 퀴즈 난이도가 성공적으로 조절되었습니다: " + request.getPatientStatus());
    }
}

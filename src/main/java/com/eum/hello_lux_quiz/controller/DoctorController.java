package com.eum.hello_lux_quiz.controller;

import com.eum.hello_lux_quiz.dto.DoctorLevelUpdateRequest;
import com.eum.hello_lux_quiz.dto.DoctorLevelUpdateResponse;
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
     * 의사 환자 난이도 조절 API
     * PUT /api/doctor/{p_code}/paitient_status
     */
    @PutMapping("/{p_code}/paitient_status")
    public ResponseEntity<DoctorLevelUpdateResponse> updatePatientStatus(
            @PathVariable("p_code") Integer pCode,
            @RequestBody DoctorLevelUpdateRequest request) {

        // Service 로직 실행 (환자 상태 변경)
        patientService.updatePatientStatus(pCode, request.getPatientStatus());

        // 명세서 응답: {"message": "난이도 변경 완료"}
        return ResponseEntity.ok(new DoctorLevelUpdateResponse("난이도 변경 완료"));
    }
}

package com.eum.hello_lux_quiz.domain.doctor.controller;

import com.eum.hello_lux_quiz.domain.doctor.dto.DoctorLinkRequest;
import com.eum.hello_lux_quiz.domain.doctor.dto.DoctorPatientResponse;
import com.eum.hello_lux_quiz.domain.doctor.dto.LevelRequest;
import com.eum.hello_lux_quiz.domain.doctor.dto.ReportResponse;
import com.eum.hello_lux_quiz.domain.doctor.dto.ReportUpdateRequest;
import com.eum.hello_lux_quiz.domain.doctor.service.DoctorService;
import com.eum.hello_lux_quiz.dto.DoctorLevelUpdateRequest;
import com.eum.hello_lux_quiz.dto.DoctorLevelUpdateResponse;
import com.eum.hello_lux_quiz.global.common.MessageResponse;
import com.eum.hello_lux_quiz.global.common.SecurityUtil;
import com.eum.hello_lux_quiz.service.PatientStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 과거 quiz_llm 의 controller.DoctorController 와 통합.
@RestController("authDoctorController")
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final PatientStatusService patientStatusService;

    /** 의사-환자 연동 */
    @PostMapping("/link")
    public ResponseEntity<MessageResponse> link(@Valid @RequestBody DoctorLinkRequest request) {
        doctorService.link(SecurityUtil.getCurrentUserId(), request.patientCode());
        return ResponseEntity.ok(MessageResponse.of("연동 완료"));
    }

    /** 담당 환자 목록 조회 */
    @GetMapping("/patients")
    public ResponseEntity<List<DoctorPatientResponse>> getPatients() {
        return ResponseEntity.ok(doctorService.getPatients(SecurityUtil.getCurrentUserId()));
    }

    /** 진료 참고 리포트 조회 */
    @GetMapping("/{pCode}/report")
    public ResponseEntity<ReportResponse> getReport(@PathVariable Integer pCode) {
        return ResponseEntity.ok(doctorService.getReport(SecurityUtil.getCurrentUserId(), pCode));
    }

    /** 난이도 조절 */
    @PutMapping("/{pCode}/level")
    public ResponseEntity<MessageResponse> updateLevel(
            @PathVariable Integer pCode,
            @Valid @RequestBody LevelRequest request) {
        doctorService.updateLevel(SecurityUtil.getCurrentUserId(), pCode, request);
        return ResponseEntity.ok(MessageResponse.of("난이도 변경 완료"));
    }

    /** 진료 리포트 작성/수정 */
    @PutMapping("/{pCode}/report")
    public ResponseEntity<MessageResponse> saveReport(
            @PathVariable Integer pCode,
            @Valid @RequestBody ReportUpdateRequest request) {
        doctorService.saveReport(SecurityUtil.getCurrentUserId(), pCode, request);
        return ResponseEntity.ok(MessageResponse.of("리포트 저장 완료"));
    }

    /**
     * 환자 인지 상태(유지/주의/위험) 변경. 이 값은 퀴즈 난이도 산정에 직접 사용된다.
     * (구버전 controller.DoctorController 의 {@code /paitient_status} 오타 경로를 정정하여 통합)
     */
    @PutMapping("/{pCode}/patient-status")
    public ResponseEntity<DoctorLevelUpdateResponse> updatePatientStatus(
            @PathVariable Integer pCode,
            @RequestBody DoctorLevelUpdateRequest request) {
        patientStatusService.updatePatientStatus(pCode, request.getPatientStatus());
        return ResponseEntity.ok(new DoctorLevelUpdateResponse("난이도 변경 완료"));
    }
}

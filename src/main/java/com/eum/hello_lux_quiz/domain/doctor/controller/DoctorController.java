package com.eum.hello_lux_quiz.domain.doctor.controller;

import com.eum.hello_lux_quiz.domain.doctor.dto.DoctorLinkRequest;
import com.eum.hello_lux_quiz.domain.doctor.dto.DoctorPatientResponse;
import com.eum.hello_lux_quiz.domain.doctor.dto.LevelRequest;
import com.eum.hello_lux_quiz.domain.doctor.dto.ReportResponse;
import com.eum.hello_lux_quiz.domain.doctor.dto.ReportUpdateRequest;
import com.eum.hello_lux_quiz.domain.doctor.service.DoctorService;
import com.eum.hello_lux_quiz.global.common.MessageResponse;
import com.eum.hello_lux_quiz.global.common.SecurityUtil;
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

// quiz_llm 의 controller.DoctorController 와 빈 이름이 겹치지 않도록 명시적 이름 지정
@RestController("authDoctorController")
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

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
}

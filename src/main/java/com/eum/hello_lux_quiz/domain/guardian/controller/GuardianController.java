package com.eum.hello_lux_quiz.domain.guardian.controller;

import com.eum.hello_lux_quiz.domain.guardian.dto.DashboardResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.LinkRequest;
import com.eum.hello_lux_quiz.domain.guardian.dto.LinkResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.LinkedPatientResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.MemoCreateResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.MemoRequest;
import com.eum.hello_lux_quiz.domain.guardian.dto.MemoResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.TrendResponse;
import com.eum.hello_lux_quiz.domain.guardian.service.GuardianService;
import com.eum.hello_lux_quiz.domain.patient.service.PatientService;
import com.eum.hello_lux_quiz.global.common.MessageResponse;
import com.eum.hello_lux_quiz.global.common.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * 보호자 API. 경로의 {@code p_code} 는 퀴즈 API 와 동일하게 6자리 연동 코드(문자열)를 받는다.
 */
@RestController
@RequestMapping("/api/guardian")
@RequiredArgsConstructor
public class GuardianController {

    private final GuardianService guardianService;
    // 연동용 코드(p_code, 예: "AB37X2")를 내부 식별자로 변환하기 위해 사용한다.
    private final PatientService patientService;

    /** 보호자-환자 연동 */
    @PostMapping("/link")
    public ResponseEntity<LinkResponse> link(@Valid @RequestBody LinkRequest request) {
        return ResponseEntity.ok(guardianService.link(SecurityUtil.getCurrentUserId(), request.patientCode()));
    }

    /** 연동된 환자 목록 조회 */
    @GetMapping("/patients")
    public ResponseEntity<List<LinkedPatientResponse>> getPatients() {
        return ResponseEntity.ok(guardianService.getLinkedPatients(SecurityUtil.getCurrentUserId()));
    }

    /** 환자 요약 대시보드 */
    @GetMapping("/{p_code}/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable("p_code") String patientCode) {
        Integer pCode = patientService.resolvePCode(patientCode);
        return ResponseEntity.ok(guardianService.getDashboard(SecurityUtil.getCurrentUserId(), pCode));
    }

    /** 환자 상태 기록(보호자 메모) 작성 — 건강/수면/식사/통증/기분/행동/연계여부/메모 */
    @PostMapping("/{p_code}/memo")
    public ResponseEntity<MemoCreateResponse> createMemo(
            @PathVariable("p_code") String patientCode,
            @Valid @RequestBody MemoRequest request) {
        Integer pCode = patientService.resolvePCode(patientCode);
        MemoCreateResponse response =
                guardianService.createMemo(SecurityUtil.getCurrentUserId(), pCode, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** 상태 메모 목록 조회 */
    @GetMapping("/{p_code}/memo")
    public ResponseEntity<List<MemoResponse>> getMemos(@PathVariable("p_code") String patientCode) {
        Integer pCode = patientService.resolvePCode(patientCode);
        return ResponseEntity.ok(guardianService.getMemos(SecurityUtil.getCurrentUserId(), pCode));
    }

    /** 상태 기록 수정 */
    @PutMapping("/{p_code}/memo/{memoId}")
    public ResponseEntity<MemoResponse> updateMemo(
            @PathVariable("p_code") String patientCode,
            @PathVariable Long memoId,
            @Valid @RequestBody MemoRequest request) {
        Integer pCode = patientService.resolvePCode(patientCode);
        return ResponseEntity.ok(
                guardianService.updateMemo(SecurityUtil.getCurrentUserId(), pCode, memoId, request));
    }

    /** 상태 기록 삭제 */
    @DeleteMapping("/{p_code}/memo/{memoId}")
    public ResponseEntity<MessageResponse> deleteMemo(
            @PathVariable("p_code") String patientCode,
            @PathVariable Long memoId) {
        Integer pCode = patientService.resolvePCode(patientCode);
        guardianService.deleteMemo(SecurityUtil.getCurrentUserId(), pCode, memoId);
        return ResponseEntity.ok(MessageResponse.of("삭제 완료"));
    }

    /** 변화 추이 조회 */
    @GetMapping("/{p_code}/trend")
    public ResponseEntity<TrendResponse> getTrend(
            @PathVariable("p_code") String patientCode,
            @RequestParam(required = false, defaultValue = "week") String period) {
        Integer pCode = patientService.resolvePCode(patientCode);
        return ResponseEntity.ok(guardianService.getTrend(SecurityUtil.getCurrentUserId(), pCode, period));
    }
}

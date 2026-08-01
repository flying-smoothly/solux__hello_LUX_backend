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

@RestController
@RequestMapping("/api/guardian")
@RequiredArgsConstructor
public class GuardianController {

    private final GuardianService guardianService;

    /** 보호자-환자 연동 */
    @PostMapping("/link")
    public ResponseEntity<LinkResponse> link(@Valid @RequestBody LinkRequest request) {
        return ResponseEntity.ok(guardianService.link(SecurityUtil.getCurrentEmail(), request.pCode()));
    }

    /** 연동된 환자 목록 조회 */
    @GetMapping("/patients")
    public ResponseEntity<List<LinkedPatientResponse>> getPatients() {
        return ResponseEntity.ok(guardianService.getLinkedPatients(SecurityUtil.getCurrentEmail()));
    }

    /** 환자 요약 대시보드 */
    @GetMapping("/{pCode}/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable Integer pCode) {
        return ResponseEntity.ok(guardianService.getDashboard(SecurityUtil.getCurrentEmail(), pCode));
    }

    /** 환자 상태 기록(보호자 메모) 작성 — 건강/수면/식사/통증/기분/행동/연계여부/메모 */
    @PostMapping("/{pCode}/memo")
    public ResponseEntity<MemoCreateResponse> createMemo(
            @PathVariable Integer pCode,
            @Valid @RequestBody MemoRequest request) {
        MemoCreateResponse response =
                guardianService.createMemo(SecurityUtil.getCurrentEmail(), pCode, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** 상태 메모 목록 조회 */
    @GetMapping("/{pCode}/memo")
    public ResponseEntity<List<MemoResponse>> getMemos(@PathVariable Integer pCode) {
        return ResponseEntity.ok(guardianService.getMemos(SecurityUtil.getCurrentEmail(), pCode));
    }

    /** 상태 기록 수정 */
    @PutMapping("/{pCode}/memo/{memoId}")
    public ResponseEntity<MemoResponse> updateMemo(
            @PathVariable Integer pCode,
            @PathVariable Long memoId,
            @Valid @RequestBody MemoRequest request) {
        return ResponseEntity.ok(
                guardianService.updateMemo(SecurityUtil.getCurrentEmail(), pCode, memoId, request));
    }

    /** 상태 기록 삭제 */
    @DeleteMapping("/{pCode}/memo/{memoId}")
    public ResponseEntity<MessageResponse> deleteMemo(
            @PathVariable Integer pCode,
            @PathVariable Long memoId) {
        guardianService.deleteMemo(SecurityUtil.getCurrentEmail(), pCode, memoId);
        return ResponseEntity.ok(MessageResponse.of("삭제 완료"));
    }

    /** 변화 추이 조회 */
    @GetMapping("/{pCode}/trend")
    public ResponseEntity<TrendResponse> getTrend(
            @PathVariable Integer pCode,
            @RequestParam(required = false, defaultValue = "week") String period) {
        return ResponseEntity.ok(guardianService.getTrend(SecurityUtil.getCurrentEmail(), pCode, period));
    }
}

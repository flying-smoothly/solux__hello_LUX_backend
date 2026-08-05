package com.eum.hello_lux_quiz.domain.patient.controller;

import com.eum.hello_lux_quiz.domain.patient.dto.DailyStatusRequest;
import com.eum.hello_lux_quiz.domain.patient.dto.DailyStatusResponse;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientCodeResponse;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientInfoResponse;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientRegisterRequest;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientRegisterResponse;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientUpdateRequest;
import com.eum.hello_lux_quiz.domain.patient.service.PatientService;
import com.eum.hello_lux_quiz.global.common.MessageResponse;
import com.eum.hello_lux_quiz.global.common.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
/**
 * 환자 API. 경로의 {@code p_code} 는 퀴즈 API 와 동일하게 6자리 연동 코드(문자열)를 받는다.
 * 내부 식별자(Integer)는 {@link PatientService#resolvePCode(String)} 로만 얻는다.
 */
@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /** 환자 기본 정보 등록 (patient) */
    @PostMapping("/register")
    public ResponseEntity<PatientRegisterResponse> register(
            @Valid @RequestBody PatientRegisterRequest request) {
        PatientRegisterResponse response = patientService.register(SecurityUtil.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** 환자 정보 조회 (patient / guardian / doctor) */
    @GetMapping("/{p_code}")
    public ResponseEntity<PatientInfoResponse> getInfo(@PathVariable("p_code") String patientCode) {
        return ResponseEntity.ok(patientService.getInfo(patientService.resolvePCode(patientCode)));
    }

    /** 환자 정보 수정 (patient) */
    @PutMapping("/{p_code}")
    public ResponseEntity<MessageResponse> update(
            @PathVariable("p_code") String patientCode,
            @RequestBody PatientUpdateRequest request) {
        Integer pCode = patientService.resolvePCode(patientCode);
        patientService.update(SecurityUtil.getCurrentUserId(), pCode, request);
        return ResponseEntity.ok(MessageResponse.of("수정 완료"));
    }

    /** 환자 코드 발급/조회 (patient) */
    @GetMapping("/{p_code}/code")
    public ResponseEntity<PatientCodeResponse> getCode(@PathVariable("p_code") String patientCode) {
        Integer pCode = patientService.resolvePCode(patientCode);
        String code = patientService.getCode(SecurityUtil.getCurrentUserId(), pCode);
        return ResponseEntity.ok(new PatientCodeResponse(code));
    }
    
    /** 환자 일일 상태(건강 체크) 입력 (patient) — 하루 1건, 재입력 시 갱신 */
    @PostMapping("/{p_code}/daily-status")
    public ResponseEntity<DailyStatusResponse> saveDailyStatus(
            @PathVariable("p_code") String patientCode,
            @RequestBody DailyStatusRequest request) {
        Integer pCode = patientService.resolvePCode(patientCode);
        DailyStatusResponse response =
                patientService.saveDailyStatus(SecurityUtil.getCurrentUserId(), pCode, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** 환자 일일 상태 조회 (patient / guardian / doctor) — date 미지정 시 오늘 */
    @GetMapping("/{p_code}/daily-status")
    public ResponseEntity<DailyStatusResponse> getDailyStatus(
            @PathVariable("p_code") String patientCode,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(patientService.getDailyStatus(patientService.resolvePCode(patientCode), date));
    }

    /** 환자 일일 상태 기록 전체 조회 (patient / guardian / doctor) */
    @GetMapping("/{p_code}/daily-status/history")
    public ResponseEntity<List<DailyStatusResponse>> getDailyStatusHistory(
            @PathVariable("p_code") String patientCode) {
        return ResponseEntity.ok(patientService.getDailyStatusHistory(patientService.resolvePCode(patientCode)));
    }
}

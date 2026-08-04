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
    @GetMapping("/{pCode}")
    public ResponseEntity<PatientInfoResponse> getInfo(@PathVariable Integer pCode) {
        return ResponseEntity.ok(patientService.getInfo(pCode));
    }

    /** 환자 정보 수정 (patient) */
    @PutMapping("/{pCode}")
    public ResponseEntity<MessageResponse> update(
            @PathVariable Integer pCode,
            @RequestBody PatientUpdateRequest request) {
        patientService.update(SecurityUtil.getCurrentUserId(), pCode, request);
        return ResponseEntity.ok(MessageResponse.of("수정 완료"));
    }

    /** 환자 코드 발급/조회 (patient) */
    @GetMapping("/{pCode}/code")
    public ResponseEntity<PatientCodeResponse> getCode(@PathVariable Integer pCode) {
        Integer code = patientService.getCode(SecurityUtil.getCurrentUserId(), pCode);
        return ResponseEntity.ok(new PatientCodeResponse(code));
    }
    
    /** 환자 일일 상태(건강 체크) 입력 (patient) — 하루 1건, 재입력 시 갱신 */
    @PostMapping("/{pCode}/daily-status")
    public ResponseEntity<DailyStatusResponse> saveDailyStatus(
            @PathVariable Integer pCode,
            @RequestBody DailyStatusRequest request) {
        DailyStatusResponse response =
                patientService.saveDailyStatus(SecurityUtil.getCurrentUserId(), pCode, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** 환자 일일 상태 조회 (patient / guardian / doctor) — date 미지정 시 오늘 */
    @GetMapping("/{pCode}/daily-status")
    public ResponseEntity<DailyStatusResponse> getDailyStatus(
            @PathVariable Integer pCode,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(patientService.getDailyStatus(pCode, date));
    }

    /** 환자 일일 상태 기록 전체 조회 (patient / guardian / doctor) */
    @GetMapping("/{pCode}/daily-status/history")
    public ResponseEntity<List<DailyStatusResponse>> getDailyStatusHistory(@PathVariable Integer pCode) {
        return ResponseEntity.ok(patientService.getDailyStatusHistory(pCode));
    }
}

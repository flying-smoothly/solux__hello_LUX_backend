package com.solux.web.domain.patient.controller;

import com.solux.web.domain.patient.dto.PatientCodeResponse;
import com.solux.web.domain.patient.dto.PatientInfoResponse;
import com.solux.web.domain.patient.dto.PatientRegisterRequest;
import com.solux.web.domain.patient.dto.PatientRegisterResponse;
import com.solux.web.domain.patient.dto.PatientUpdateRequest;
import com.solux.web.domain.patient.service.PatientService;
import com.solux.web.global.common.MessageResponse;
import com.solux.web.global.common.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /** 환자 기본 정보 등록 (patient) */
    @PostMapping("/register")
    public ResponseEntity<PatientRegisterResponse> register(
            @Valid @RequestBody PatientRegisterRequest request) {
        PatientRegisterResponse response = patientService.register(SecurityUtil.getCurrentEmail(), request);
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
        patientService.update(SecurityUtil.getCurrentEmail(), pCode, request);
        return ResponseEntity.ok(MessageResponse.of("수정 완료"));
    }

    /** 환자 코드 발급/조회 (patient) */
    @GetMapping("/{pCode}/code")
    public ResponseEntity<PatientCodeResponse> getCode(@PathVariable Integer pCode) {
        Integer code = patientService.getCode(SecurityUtil.getCurrentEmail(), pCode);
        return ResponseEntity.ok(new PatientCodeResponse(code));
    }
}

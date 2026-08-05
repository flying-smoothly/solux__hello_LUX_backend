package com.eum.hello_lux_quiz.controller;

import com.eum.hello_lux_quiz.domain.patient.service.PatientService;

import com.eum.hello_lux_quiz.dto.*;
import com.eum.hello_lux_quiz.service.LifeDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 삶의 DB API (퀴즈 API 와 동일하게 6자리 연동 코드(문자열)를 받는다.)
*/

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class LifeDbController {

    // 연동용 코드(p_code, 예: "AB37X2")를 내부 식별자로 변환하기 위해 사용한다.
    private final LifeDbService lifeDbService;

    // 1. 환자 삶의 DB 최초 등록
    @PostMapping("/{p_code}/memories")
    public ResponseEntity<?> createLifeDb(
            @PathVariable("p_code") String patientCode,
            @RequestBody LifeDbRequestDto request) {

        Integer memoryId = lifeDbService.saveLifeDb(pCode, request);

        Map<String, Object> response = Map.of(
                "p_code", pCode,
                "memory_id", memoryId,
                "message", "삶의 DB 저장"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. 환자 삶의 DB 조회
    @GetMapping("/{p_code}/memories/{memory_id}")
    public ResponseEntity<LifeDbResponseDto> getLifeDb(
            @PathVariable("p_code") Integer pCode,
            @PathVariable("memory_id") Integer memoryId) {

        LifeDbResponseDto response = lifeDbService.getLifeDb(pCode, memoryId);
        return ResponseEntity.ok(response);
    }

    // 3. 환자 삶의 DB 사건 추가
    @PostMapping("/{p_code}/memories/{memory_id}")
    public ResponseEntity<?> addLifeDbEvent(
            @PathVariable("p_code") Integer pCode,
            @PathVariable("memory_id") Integer memoryId,
            @RequestBody LifeDbEventRequestDto request) {

        Integer eventId = lifeDbService.addEventToLifeDb(pCode, memoryId, request);

        Map<String, Object> response = Map.of(
                "event_id", eventId,
                "memory_id", memoryId,
                "message", "새로운 주요 사건이 타임라인에 추가되었습니다."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 4. 삶의 DB 수정
    @PatchMapping("/{p_code}/memories")
    public ResponseEntity<?> updateLifeDb(
            @PathVariable("p_code") Integer pCode,
            @RequestBody LifeDbUpdateRequestDto request) {

        Integer updatedMemoryId = lifeDbService.updateLifeDb(pCode, request);

        Map<String, Object> response = Map.of(
                "memory_id", updatedMemoryId,
                "message", "삶의 DB 기본 정보가 성공적으로 수정되었습니다."
        );
        return ResponseEntity.ok(response);
    }
}

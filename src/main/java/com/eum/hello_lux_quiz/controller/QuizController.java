package com.eum.hello_lux_quiz.controller;

import com.eum.hello_lux_quiz.domain.patient.service.PatientService;
import com.eum.hello_lux_quiz.dto.*;
import com.eum.hello_lux_quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;
    // 연동용 코드(patient_code, 예: "AB37X2")를 내부 환자 코드(p_code, Integer)로 변환하기 위해 사용한다.
    private final PatientService patientService;

    // ==========================================
    // 1. 오늘의 맞춤 퀴즈 조회
    // GET /api/quiz/{p_code}/today
    // ==========================================
    @GetMapping("/quiz/{p_code}/today")
    public ResponseEntity<List<QuizItemDto>> getTodayQuiz(
            @PathVariable("p_code") String patientCode) {

        Integer pCode = patientService.resolvePCode(patientCode);
        List<QuizItemDto> todayQuizzes = quizService.getOrCreateTodayQuiz(pCode, "");

        return ResponseEntity.ok(todayQuizzes);
    }

    // ==========================================
    // 2. 퀴즈 응답 제출 (단건 채점 및 저장)
    // POST /api/quiz/{p_code}/{set_id}/{quiz_num}/answer
    // ==========================================
    @PostMapping("/quiz/{p_code}/{set_id}/{quiz_num}/answer")
    public ResponseEntity<QuizAnswerResponse> submitAnswer(
            @PathVariable("p_code") String patientCode,
            @PathVariable("set_id") Integer setId,
            @PathVariable("quiz_num") Integer quizNum,
            @RequestBody QuizAnswerRequest request) {

        Integer pCode = patientService.resolvePCode(patientCode);
        QuizAnswerResponse response = quizService.processAnswer(pCode, setId, quizNum, request.getAnswer());
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 3. 특정 환자 날짜별 퀴즈 결과 조회
    // GET /api/patients/{p_code}/results/{date} (또는 쿼리 파라미터 지원)
    // ==========================================
    @GetMapping({"/patients/{p_code}/results/{date}", "/patients/{p_code}/results"})
    public ResponseEntity<QuizResultResponse> getQuizResult(
            @PathVariable("p_code") String patientCode, 
            @PathVariable(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePath,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateQuery) {

        Integer pCode = patientService.resolvePCode(patientCode);
        LocalDate targetDate = (datePath != null) ? datePath : (dateQuery != null ? dateQuery : LocalDate.now());

        QuizResultResponse response = quizService.getQuizResultByDate(pCode, targetDate);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 4. 환자 마이페이지 / 전체 결과 및 피드백 관련
    // ==========================================
    /**
     * GET /api/patients/{p_code}/quizSet/{set_id}/feedbacks 특정 퀴즈 세트 피드백 조회
     */
    @GetMapping("/patients/{p_code}/quizSet/{set_id}/feedbacks")
    public ResponseEntity<List<QuizFeedbackResponse>> getQuizFeedback(
            @PathVariable("p_code") String patientCode,
            @PathVariable("set_id") Integer setId) {

        // 피드백은 set_id 로 조회하지만, 경로의 연동 코드가 유효한지 함께 검증한다.
        patientService.resolvePCode(patientCode);

        List<QuizFeedbackResponse> response = quizService.getQuizFeedback(setId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/patients/{p_code}/quiz-results 퀴즈 기록 전체/기간 조회
     */
    @GetMapping("/patients/{p_code}/quiz-results")
    public ResponseEntity<List<QuizResultResponse>> getAllQuizResults(
            @PathVariable("p_code") String patientCode,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        Integer pCode = patientService.resolvePCode(patientCode);
        List<QuizResultResponse> response = quizService.getAllQuizResultsByPCode(pCode, from, to);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 5. 퀴즈 세트 전체 최종 제출 처리 API
    // POST /api/quiz/result/submit
    // ==========================================
    @PostMapping("/quiz/result")
    public ResponseEntity<Map<String, String>> submitQuizResult(
            @RequestBody QuizResultSubmitRequest request) {

        quizService.submitQuizResult(request, "");
        return ResponseEntity.ok(Map.of("message", "퀴즈 결과 제출 및 다음 세트 생성이 완료되었습니다."));
    }
}

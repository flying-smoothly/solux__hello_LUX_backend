package com.eum.hello_lux_quiz.controller;

import com.eum.hello_lux_quiz.dto.*;
import com.eum.hello_lux_quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;

    // ==========================================
    // 1. 오늘의 맞춤 퀴즈 조회
    // GET /api/quiz/{p_code}/today
    // ==========================================
    @GetMapping("/quiz/{p_code}/today")
    public ResponseEntity<List<QuizItemDto>> getTodayQuiz(
            @PathVariable("p_code") Long pCode) {

        List<QuizItemDto> todayQuizzes = quizService.getOrCreateTodayQuiz(pCode.intValue(), "");
        return ResponseEntity.ok(todayQuizzes);
    }

    // ==========================================
    // 2. 퀴즈 응답 제출 (단건 채점 및 저장)
    // POST /api/quiz/{p_code}/{set_id}/{quiz_num}/answer
    // ==========================================
    @PostMapping("/quiz/{p_code}/{set_id}/{quiz_num}/answer")
    public ResponseEntity<QuizAnswerResponse> submitAnswer(
            @PathVariable("p_code") Long pCode,
            @PathVariable("set_id") Integer setId, // Long -> Integer 변경
            @PathVariable("quiz_num") Integer quizNum,
            @RequestBody QuizAnswerRequest request) {

        // Body에 담겨온 answer 단답형 문자열 전달
        QuizAnswerResponse response = quizService.processAnswer(pCode.intValue(), setId, quizNum, request.getAnswer());
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 3. 특정 환자 날짜별 퀴즈 결과 조회
    // GET /api/patients/{p_code}/results/{date} (또는 쿼리 파라미터 지원)
    // ==========================================
    @GetMapping({"/patients/{p_code}/results/{date}", "/patients/{p_code}/results"})
    public ResponseEntity<QuizResultResponse> getQuizResult(
            @PathVariable("p_code") Long pCode,
            @PathVariable(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePath,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateQuery) {

        // Path or Query 둘 중 들어온 날짜 적용 (기본값: 오늘 날짜)
        LocalDate targetDate = (datePath != null) ? datePath : (dateQuery != null ? dateQuery : LocalDate.now());

        QuizResultResponse response = quizService.getQuizResultByDate(pCode.intValue(), targetDate);
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
            @PathVariable("p_code") Long pCode,
            @PathVariable("set_id") Integer setId) { // Long -> Integer 변경

        List<QuizFeedbackResponse> response = quizService.getQuizFeedback(setId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/patients/{p_code}/quiz-results 퀴즈 기록 전체 조회
     */
    @GetMapping("/patients/{p_code}/quiz-results")
    public ResponseEntity<List<QuizResultResponse>> getAllQuizResults(
            @PathVariable("p_code") Long pCode) {

        List<QuizResultResponse> response = quizService.getAllQuizResultsByPCode(pCode.intValue());
        return ResponseEntity.ok(response);
    }
}

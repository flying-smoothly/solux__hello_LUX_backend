package com.eum.hello_lux_quiz.service;

import com.eum.hello_lux_quiz.domain.*;
import com.eum.hello_lux_quiz.dto.*;
import com.eum.hello_lux_quiz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class QuizService {

    private final QuizSetRepository quizSetRepository;
    private final QuizItemRepository quizItemRepository;
    private final QuizResultRepository quizResultRepository;
    private final QuizFeedbackRepository quizFeedbackRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final QuizGeneratorService quizGeneratorService;
    private final QuizScoringService quizScoringService;

    public QuizService(QuizSetRepository quizSetRepository,
            QuizItemRepository quizItemRepository,
            QuizResultRepository quizResultRepository,
            QuizFeedbackRepository quizFeedbackRepository,
            PatientProfileRepository patientProfileRepository,
            QuizGeneratorService quizGeneratorService,
            QuizScoringService quizScoringService) {
        this.quizSetRepository = quizSetRepository;
        this.quizItemRepository = quizItemRepository;
        this.quizResultRepository = quizResultRepository;
        this.quizFeedbackRepository = quizFeedbackRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.quizGeneratorService = quizGeneratorService;
        this.quizScoringService = quizScoringService;
    }

    /**
     * 오늘의 퀴즈 목록 조회 (없으면 생성)
     */
    @Transactional
    public List<QuizItemDto> getOrCreateTodayQuiz(Integer pCode, String lifeDbContext) {
        // 💡 1. 환자의 기존 퀴즈 세트 목록 조회 후, 없으면 새로 생성 (무한 중복 생성 방지)
        // (만약 최신 퀴즈 세트 재사용 로직이 필요 없다면 createQuizSet을 바로 호출해도 됩니다)
        QuizSet savedQuizSet = createQuizSet(pCode, lifeDbContext);
        List<QuizItem> items = quizItemRepository.findBySetId(savedQuizSet.getSetId());

        List<QuizItemDto> resultList = new ArrayList<>();
        for (QuizItem item : items) {
            QuizItemDto dto = new QuizItemDto();

            dto.setSetId(savedQuizSet.getSetId());
            dto.setPCode(pCode);

            dto.setQuizNum(item.getQuizNum());
            dto.setLevel(item.getLevel());
            dto.setQuizCategory(item.getQuizCategory());
            dto.setQuizComment(item.getQuizComment());
            dto.setQuizPhoto(item.getQuizPhoto());
            dto.setAnswer(item.getAnswer());
            if (item.getOptions() != null && !item.getOptions().isBlank()) {
                dto.setOptions(List.of(item.getOptions().split(",\\s*")));
            }
            resultList.add(dto);
        }

        return resultList;
    }

    /**
     * 1. 퀴즈 문제 1개씩 풀 때마다 정답 여부 확인 및 채점
     */
    @Transactional
    public QuizAnswerResponse processAnswer(Integer pCode, Integer setId, Integer quizNum, String userAnswer) {
        QuizItem item = quizItemRepository.findBySetIdAndQuizNum(setId, quizNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈 문항을 찾을 수 없습니다. quizNum: " + quizNum));

        return quizScoringService.scoreAnswer(
                item.getQuizComment(),
                item.getAnswer(),
                userAnswer
        );
    }

    /**
     * 2. 모든 퀴즈 완료 시 제출 처리 + 3. 다음 퀴즈 세트 미리 자동 생성
     */
    @Transactional
    public void submitQuizResult(QuizResultSubmitRequest request, String lifeDbContext) {
        // (1) 퀴즈 결과 DB 저장
        QuizResult quizResult = new QuizResult(
                request.getSetId(),
                request.getPCode(),
                LocalDate.now(),
                request.getTotalCount(),
                request.getCorrectCount(),
                request.getHint(),
                request.getCaculate()
        );
        quizResultRepository.save(quizResult);

        // (2) 피드백 저장
        if (request.getFeedbackContent() != null && !request.getFeedbackContent().isBlank()) {
            QuizFeedback quizFeedback = new QuizFeedback(
                    request.getSetId(),
                    request.getFeedbackContent(),
                    LocalDate.now()
            );
            quizFeedbackRepository.save(quizFeedback);
        }

        // (3) 다음 풀 퀴즈 세트 미리 자동 생성
        createQuizSet(request.getPCode(), lifeDbContext);
    }

    /**
     * 퀴즈 세트 및 문항 생성 메서드
     */
    @Transactional
    public QuizSet createQuizSet(Integer pCode, String lifeDbContext) {
        QuizSet quizSet = new QuizSet(pCode, LocalTime.now(), LocalTime.now(), 0);
        QuizSet savedQuizSet = quizSetRepository.save(quizSet);

        // 환자 프로필 조회
        PatientProfile profile = patientProfileRepository.findByPCode(pCode)
                .orElseThrow(() -> new IllegalArgumentException("환자 프로필을 찾을 수 없습니다. pCode=" + pCode));

        String patientStatus = profile.getPatientStatus() != null ? profile.getPatientStatus() : "유지";

        // LLM 퀴즈 생성 호출
        List<GeneratedQuizItemDto> generatedItems = quizGeneratorService.generateQuizSet(patientStatus, profile, lifeDbContext);

        // 💡 2. quizNum을 1부터 순차적으로 부여하도록 수정 (기존 하드코딩 0 수정)
        int quizNumCounter = 1;
        for (GeneratedQuizItemDto dto : generatedItems) {
            String category = (dto.getQuizCategory() != null) ? dto.getQuizCategory() : "text";

            String optionsString = (dto.getOptions() != null && !dto.getOptions().isEmpty())
                    ? String.join(", ", dto.getOptions())
                    : null;

            QuizItem item = new QuizItem(
                    savedQuizSet.getSetId(),
                    pCode,
                    quizNumCounter++, // 💡 0 대신 1, 2, 3... 번호 자동 할당
                    category,
                    dto.getLevel(),
                    dto.getQuizComment(),
                    dto.getQuizPhoto(),
                    dto.getAnswer(),
                    optionsString
            );

            quizItemRepository.save(item);
        }

        return savedQuizSet;
    }

    public QuizResultResponse getQuizResultByDate(Integer pCode, LocalDate date) {
        QuizResult quizResult = quizResultRepository.findByPCodeAndDate(pCode, date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 퀴즈 결과를 찾을 수 없습니다. (pCode: " + pCode + ", date: " + date + ")"));

        return QuizResultResponse.from(quizResult, 0);
    }

    public List<QuizFeedbackResponse> getQuizFeedback(Integer setId) {
        QuizFeedback feedback = quizFeedbackRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈 세트의 피드백이 존재하지 않습니다. ID: " + setId));

        List<QuizFeedbackResponse> list = new ArrayList<>();
        list.add(new QuizFeedbackResponse(
                feedback.getFeedbackId(),
                feedback.getSetId(),
                feedback.getFeedbackContent(),
                feedback.getCreatedAt() != null ? feedback.getCreatedAt().toString() : null
        ));

        return list;
    }

    public List<QuizResultResponse> getAllQuizResultsByPCode(Integer pCode) {
        List<QuizResult> results = quizResultRepository.findByPCode(pCode);

        List<QuizResultResponse> responseList = new ArrayList<>();
        for (QuizResult result : results) {
            responseList.add(QuizResultResponse.from(result, 0));
        }

        return responseList;
    }

    public QuizSet getQuizSet(Integer setId) {
        return quizSetRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈 세트가 존재하지 않습니다. ID: " + setId));
    }
}

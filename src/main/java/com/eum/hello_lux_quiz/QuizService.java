package com.eum.hello_lux_quiz.service;

import com.eum.hello_lux_quiz.domain.*;
import com.eum.hello_lux_quiz.dto.*;
import com.eum.hello_lux_quiz.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QuizService {

    private final QuizSetRepository quizSetRepository;
    private final QuizItemRepository quizItemRepository;
    private final QuizResultRepository quizResultRepository;
    private final QuizFeedbackRepository quizFeedbackRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final LifeDbRepository lifeDbRepository;
    private final DetailRepository detailRepository; // 세분화 Repository
    private final QuizGeneratorService quizGeneratorService;
    private final QuizScoringService quizScoringService;
    private final ObjectMapper objectMapper; // JSON 변환용 ObjectMapper

    public QuizService(QuizSetRepository quizSetRepository,
            QuizItemRepository quizItemRepository,
            QuizResultRepository quizResultRepository,
            QuizFeedbackRepository quizFeedbackRepository,
            PatientProfileRepository patientProfileRepository,
            LifeDbRepository lifeDbRepository,
            DetailRepository detailRepository,
            QuizGeneratorService quizGeneratorService,
            QuizScoringService quizScoringService,
            ObjectMapper objectMapper) {
        this.quizSetRepository = quizSetRepository;
        this.quizItemRepository = quizItemRepository;
        this.quizResultRepository = quizResultRepository;
        this.quizFeedbackRepository = quizFeedbackRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.lifeDbRepository = lifeDbRepository;
        this.detailRepository = detailRepository;
        this.quizGeneratorService = quizGeneratorService;
        this.quizScoringService = quizScoringService;
        this.objectMapper = objectMapper;
    }

    /**
     * DB에서 삶의DB + 세분화(랜덤 3개) 정보를 조회하여 Context 문자열을 생성하는 메서드
     */
    public String buildLifeDbContext(Integer pCode) {
        List<LifeDb> lifeDbList = lifeDbRepository.findByPCode(pCode);
        if (lifeDbList.isEmpty()) {
            return "";
        }

        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("=== 환자 회상 정보 (삶의DB 및 세분화 사건) ===\n");

        for (LifeDb memory : lifeDbList) {
            String formattedFamily = formatFamilyInfo(memory.getFamily());

            contextBuilder.append(String.format("""
                - 고향: %s / 직업: %s / 가족: %s
                - 좋아하는 것: %s / 주요 장소: %s / 제목: %s
                """,
                    memory.getHometown(),
                    memory.getJob(),
                    formattedFamily,
                    memory.getLikes(),
                    memory.getPlace(),
                    memory.getTitle()
            ));

            // 세분화(DetailEvent) 데이터 조회 후 셔플 -> 최대 3개 선택
            List<DetailEvent> detailList = detailRepository.findByMemoryId(memory.getMemoryId());
            if (detailList != null && !detailList.isEmpty()) {
                List<DetailEvent> shuffledList = new ArrayList<>(detailList);
                Collections.shuffle(shuffledList);
                List<DetailEvent> selectedDetails = shuffledList.stream().limit(3).toList();

                contextBuilder.append("  [연관 세부 사건 (랜덤 3개)]\n");
                for (DetailEvent detail : selectedDetails) {
                    contextBuilder.append(String.format("  * 내용: %s / 사진URL: %s / 감정: %s\n",
                            detail.getEvent(),
                            detail.getPhotoUrl() != null ? detail.getPhotoUrl() : "없음",
                            detail.getCategory()
                    ));
                }
            }
            contextBuilder.append("-----------------------\n");
        }

        return contextBuilder.toString();
    }

    // JSON [family] 텍스트를 읽기 편한 "이름(호칭)" 문자열로 변환하는 헬퍼 메서드
    private String formatFamilyInfo(String familyJson) {
        if (familyJson == null || familyJson.isBlank()) {
            return "없음";
        }
        try {
            List<FamilyDto> familyList = objectMapper.readValue(familyJson, new TypeReference<List<FamilyDto>>() {
            });
            if (familyList.isEmpty()) {
                return "없음";
            }
            return familyList.stream()
                    .map(f -> String.format("%s (%s)", f.getName(), f.getRelation()))
                    .collect(Collectors.joining(", "));
        } catch (JsonProcessingException e) {
            return familyJson;
        }
    }

    /**
     * 오늘의 퀴즈 목록 조회 (없으면 생성)
     */
    @Transactional
    public List<QuizItemDto> getOrCreateTodayQuiz(Integer pCode, String lifeDbContext) {
        String finalContext = (lifeDbContext != null && !lifeDbContext.isBlank())
                ? lifeDbContext
                : buildLifeDbContext(pCode);

        QuizSet savedQuizSet = createQuizSet(pCode, finalContext);
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

            if (item.getHints() != null && !item.getHints().isBlank()) {
                dto.setHints(List.of(item.getHints().split(",\\s*")));
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

        if (request.getFeedbackContent() != null && !request.getFeedbackContent().isBlank()) {
            QuizFeedback quizFeedback = new QuizFeedback(
                    request.getSetId(),
                    request.getFeedbackContent(),
                    LocalDate.now()
            );
            quizFeedbackRepository.save(quizFeedback);
        }

        String finalContext = (lifeDbContext != null && !lifeDbContext.isBlank())
                ? lifeDbContext
                : buildLifeDbContext(request.getPCode());

        createQuizSet(request.getPCode(), finalContext);
    }

    /**
     * 퀴즈 세트 및 문항 생성 메서드
     */
    @Transactional
    public QuizSet createQuizSet(Integer pCode, String lifeDbContext) {
        String finalContext = (lifeDbContext != null && !lifeDbContext.isBlank())
                ? lifeDbContext
                : buildLifeDbContext(pCode);

        int nextSetCount = (int) quizSetRepository.countByPCode(pCode) + 1;

        QuizSet quizSet = new QuizSet(pCode, LocalTime.now(), LocalTime.now(), 0);
        QuizSet savedQuizSet = quizSetRepository.save(quizSet);

        // 환자 프로필 조회
        PatientProfile profile = patientProfileRepository.findByPCode(pCode)
                .orElseThrow(() -> new IllegalArgumentException("환자 프로필을 찾을 수 없습니다. pCode=" + pCode));

        String patientStatus = profile.getPatientStatus() != null ? profile.getPatientStatus() : "유지";

        // 4번째 세트 주기마다 지남력 평가 질문 포함 지시문 작성
        String timeOrientationInstruction = "";
        if (nextSetCount % 4 == 0) {
            timeOrientationInstruction = "★ [지남력 질문 필수 포함]: 이번 세트는 4세트 주기에 해당하므로, Level 3 문항 중 1개는 반드시 현재 또는 과거 특정 시점의 [년, 월, 계절]을 묻는 질문(시간 지남력 평가)으로 출제하세요. (단, 날짜 오차 방지를 위해 특정 '일자'보다는 '년/월/계절' 위주로 출제)";
        }

        // 현재 기준 날짜 컨텍스트
        LocalDate today = LocalDate.now();
        String todayContext = String.format("\n[현재 기준 날짜 정보: 오늘은 %d년 %d월입니다. 퀴즈 정답 생성 시 이 날짜 정보를 참고하세요.]",
                today.getYear(), today.getMonthValue());

        // LLM 퀴즈 생성 호출
        List<GeneratedQuizItemDto> generatedItems = quizGeneratorService.generateQuizSet(
                patientStatus, profile, finalContext + todayContext, timeOrientationInstruction
        );

        int quizNumCounter = 1;
        for (GeneratedQuizItemDto dto : generatedItems) {
            String category = (dto.getQuizCategory() != null) ? dto.getQuizCategory() : "text";

            String optionsString = (dto.getOptions() != null && !dto.getOptions().isEmpty())
                    ? String.join(", ", dto.getOptions())
                    : null;

            String hintsString = (dto.getHints() != null && !dto.getHints().isEmpty())
                    ? String.join(", ", dto.getHints())
                    : null;

            // ⭕ quizNumCounter++ 인자가 세트 내 문제 순번으로 들어갑니다.
            QuizItem item = new QuizItem(
                    savedQuizSet.getSetId(),
                    pCode,
                    quizNumCounter++,
                    category,
                    dto.getLevel(),
                    dto.getQuizComment(),
                    dto.getQuizPhoto(),
                    dto.getAnswer(),
                    optionsString,
                    hintsString
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

    public List<QuizResultResponse> getAllQuizResultsByPCode(Integer pCode, LocalDate from, LocalDate to) {
        List<QuizResult> results;

        if (from != null && to != null) {
            results = quizResultRepository.findByPCodeAndDateBetween(pCode, from, to);
        } else {
            results = quizResultRepository.findByPCode(pCode);
        }

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

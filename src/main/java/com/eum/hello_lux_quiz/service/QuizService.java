package com.eum.hello_lux_quiz.service;

import com.eum.hello_lux_quiz.domain.*;
import com.eum.hello_lux_quiz.dto.*;
import com.eum.hello_lux_quiz.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Slf4j
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
     *
     * [수정] LLM이 사진 URL을 직접 베끼다가 가짜 URL(example.com 등)을 만들어내는 문제를 막기 위해, 실제 URL
     * 문자열 대신 [사진ID: N] 참조번호만 컨텍스트에 노출한다. 실제 URL은 LLM이 반환한 photoId로 서버가 DB에서 직접
     * 조회해서 매핑한다 (createQuizSet 참고).
     */
    public String buildLifeDbContext(Integer pCode) {
        List<LifeDb> lifeDbList = lifeDbRepository.findByPCode(pCode);
        if (lifeDbList.isEmpty()) {
            log.warn("===> [buildLifeDbContext] pCode: {}에 해당하는 LifeDb 데이터가 없습니다.", pCode);
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
                    boolean hasPhoto = detail.getPhotoUrl() != null && !detail.getPhotoUrl().isBlank();
                    if (hasPhoto) {
                        // ✅ URL 대신 사진ID(참조번호)만 노출 → LLM이 URL을 직접 베끼거나 지어낼 여지 차단
                        contextBuilder.append(String.format("  * [사진ID: %d] 내용: %s / 사진: 있음 / 감정: %s\n",
                                detail.getEventId(),
                                detail.getEvent(),
                                detail.getCategory()
                        ));
                    } else {
                        contextBuilder.append(String.format("  * 내용: %s / 사진: 없음 / 감정: %s\n",
                                detail.getEvent(),
                                detail.getCategory()
                        ));
                    }
                }
            }
            contextBuilder.append("-----------------------\n");
        }

        return contextBuilder.toString();
    }

    /**
     * 컨텍스트 문자열 안에 실제로 사용 가능한 사진(사진ID) 개수를 센다. lifeDbContext가 외부(컨트롤러 등)에서 미리
     * 만들어져 전달된 경우에도 동일하게 카운트할 수 있도록 문자열 마커([사진ID:) 기반으로 계산한다.
     */
    private int countAvailablePhotos(String context) {
        if (context == null || context.isBlank()) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = context.indexOf("[사진ID:", idx)) != -1) {
            count++;
            idx += 6;
        }
        return count;
    }

    /**
     * patient_status별로 요구되는 유형2(사진선택형) 문항 개수
     */
    private int requiredPhotoCountFor(String patientStatus) {
        if (patientStatus == null) {
            return 2;
        }
        return switch (patientStatus) {
            case "주의" ->
                4;
            case "위험" ->
                0;
            default ->
                2; // 유지
        };
    }

    /**
     * 사진 후보 개수가 부족할 때, LLM에게 유형2 비중을 줄이고 유형1/유형3으로 대체하라고 지시하는 문구를 생성한다. 사진이
     * 충분하면 빈 문자열을 반환한다.
     */
    private String buildPhotoAvailabilityInstruction(String patientStatus, int availablePhotoCount) {
        int requiredPhotoCount = requiredPhotoCountFor(patientStatus);
        if (availablePhotoCount >= requiredPhotoCount) {
            return "";
        }
        int fallbackCount = requiredPhotoCount - availablePhotoCount;
        return String.format(
                "★ [사진 부족 예외 처리]: 현재 사용 가능한 사진(사진ID)이 %d개뿐입니다. "
                + "유형2(사진선택형)는 최대 %d개까지만 출제하고, 부족한 %d개는 유형1(객관식) 또는 유형3(단답형)으로 대체하여 "
                + "총 7문항 구성을 유지하세요. 사진ID가 없는 문항에는 quiz_photo를 반드시 null로 설정하세요.",
                availablePhotoCount, availablePhotoCount, fallbackCount
        );
    }

    /**
     * LLM이 반환한 quiz_photo 값(사진ID 문자열)을 안전하게 정수로 파싱한다. URL이나 이상한 문자열이 들어와도 예외 없이
     * null을 반환한다 (LLM이 지시를 어긴 경우에 대한 안전장치).
     */
    private Integer tryParsePhotoId(String raw) {
        if (raw == null || raw.isBlank() || "null".equalsIgnoreCase(raw.trim())) {
            return null;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            log.warn("===> [tryParsePhotoId] quiz_photo 값이 유효한 사진ID(정수)가 아님: {}", raw);
            return null;
        }
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
            log.warn("===> [formatFamilyInfo] Family JSON 파싱 실패: {}", familyJson);
            return familyJson;
        }
    }

    /**
     * 오늘의 퀴즈 목록 조회 (없으면 생성) [수정 1] 무조건 새 세트를 생성하던 버그 수정 → 오늘 날짜로 QuizResult가 이미
     * 있으면 해당 setId의 세트 재사용 → 없으면 새로 생성 (같은 날 중단 후 재접속 시에도 기존 세트 유지)
     */
    @Transactional
    public List<QuizItemDto> getOrCreateTodayQuiz(Integer pCode, String lifeDbContext) {
        log.info("===> [getOrCreateTodayQuiz] pCode: {} 퀴즈 조회/생성 시작", pCode);

        String finalContext = (lifeDbContext != null && !lifeDbContext.isBlank())
                ? lifeDbContext
                : buildLifeDbContext(pCode);

        // ✅ 오늘 날짜로 제출된 QuizResult가 있으면 → 해당 setId의 세트 재사용
        QuizSet savedQuizSet;
        Optional<QuizResult> todayResult = quizResultRepository.findBypCodeAndDate(pCode, LocalDate.now());

        if (todayResult.isPresent()) {
            // 오늘 이미 결과가 있으면 해당 세트 재사용
            Integer existingSetId = todayResult.get().getSetId();
            savedQuizSet = quizSetRepository.findById(existingSetId)
                    .orElseGet(() -> createQuizSet(pCode, finalContext));
            log.info("===> [getOrCreateTodayQuiz] 오늘 기존 세트 재사용: setId={}", savedQuizSet.getSetId());
        } else {
            // 오늘 결과가 없으면 새 세트 생성
            savedQuizSet = createQuizSet(pCode, finalContext);
            log.info("===> [getOrCreateTodayQuiz] 새 세트 생성 완료: setId={}", savedQuizSet.getSetId());
        }

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
     * 1. 퀴즈 문제 1개씩 풀 때마다 정답 여부 확인 및 채점 (pCode, setId, quizNum 3가지 복합키 조건으로 조회)
     */
    @Transactional
    public QuizAnswerResponse processAnswer(Integer pCode, Integer setId, Integer quizNum, String userAnswer) {
        QuizItem item = quizItemRepository.findByPCodeAndSetIdAndQuizNum(pCode, setId, quizNum)
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
        // [보완] Integer -> int 언박싱 시 발생할 수 있는 NPE 및 Null Safe 방어 코드 적용
        int totalCount = (request.getTotalCount() != null) ? request.getTotalCount() : 0;
        int correctCount = (request.getCorrectCount() != null) ? request.getCorrectCount() : 0;
        int hint = (request.getHint() != null) ? request.getHint() : 0;
        String caculate = (request.getCaculate() != null) ? request.getCaculate() : "0";

        QuizResult quizResult = new QuizResult(
                request.getSetId(),
                request.getPCode(),
                LocalDate.now(),
                totalCount,
                correctCount,
                hint,
                caculate,
                request.getSuccessRate(),
                request.getHintUsed(),
                request.getAvgResponseTime(),
                request.getHealthStatus(),
                request.getSleepStatus(),
                request.getEmotionStatus()
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
     * 퀴즈 세트 및 문항 생성 메서드 (실제 LLM 연동 디버깅 로깅 적용)
     */
    @Transactional
    public QuizSet createQuizSet(Integer pCode, String lifeDbContext) {
        try {
            log.info("===> [createQuizSet] pCode: {} 세트 생성 로직 시작", pCode);

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

            // ✅ [사진 부족 예외 처리] 사용 가능한 사진(사진ID) 개수를 세어, 부족하면 유형2 비중을 낮추도록 지시문 생성
            int availablePhotoCount = countAvailablePhotos(finalContext);
            String photoAvailabilityInstruction = buildPhotoAvailabilityInstruction(patientStatus, availablePhotoCount);
            log.info("===> [createQuizSet] 사용 가능한 사진 개수: {}, 사진 부족 지시문 적용 여부: {}",
                    availablePhotoCount, !photoAvailabilityInstruction.isBlank());

            log.info("===> [QuizGeneratorService 호출 시작] patientStatus: {}", patientStatus);

            // LLM 퀴즈 생성 호출
            List<GeneratedQuizItemDto> generatedItems = quizGeneratorService.generateQuizSet(
                    patientStatus, profile, finalContext + todayContext, timeOrientationInstruction, photoAvailabilityInstruction
            );

            log.info("===> [LLM 응답 받아옴] 생성된 문항 개수: {}", generatedItems != null ? generatedItems.size() : 0);

            // [보완] LLM 응답의 Null Pointer 방어
            if (generatedItems != null) {
                int quizNumCounter = 1;
                for (GeneratedQuizItemDto dto : generatedItems) {
                    String category = (dto.getQuizCategory() != null) ? dto.getQuizCategory() : "text";

                    // ✅ [안전장치] quiz_photo는 이제 URL이 아니라 사진ID(정수)로 받는다.
                    // LLM이 지시를 어기고 URL이나 이상한 값을 반환하더라도, 여기서 안전하게 걸러내고
                    // 실제 URL은 서버가 DB에서 직접 조회해서 채운다. 유효하지 않으면 text로 강제 전환한다.
                    String resolvedPhotoUrl = null;
                    if ("photo".equals(category)) {
                        Integer photoId = tryParsePhotoId(dto.getQuizPhoto());
                        if (photoId != null) {
                            resolvedPhotoUrl = detailRepository.findById(photoId)
                                    .map(DetailEvent::getPhotoUrl)
                                    .filter(url -> url != null && !url.isBlank())
                                    .orElse(null);
                        }

                        if (resolvedPhotoUrl == null) {
                            log.warn("===> [createQuizSet] 유효하지 않은 사진ID({}) 감지 → 해당 문항을 text 유형으로 강제 전환",
                                    dto.getQuizPhoto());
                            category = "text";
                        }
                    }

                    String optionsString = (dto.getOptions() != null && !dto.getOptions().isEmpty())
                            ? String.join(", ", dto.getOptions())
                            : null;

                    String hintsString = (dto.getHints() != null && !dto.getHints().isEmpty())
                            ? String.join(", ", dto.getHints())
                            : null;

                    QuizItem item = new QuizItem(
                            savedQuizSet.getSetId(),
                            pCode,
                            quizNumCounter++,
                            category,
                            dto.getLevel(),
                            dto.getQuizComment(),
                            resolvedPhotoUrl,
                            dto.getAnswer(),
                            optionsString,
                            hintsString
                    );

                    quizItemRepository.save(item);
                }
            }

            return savedQuizSet;

        } catch (Exception e) {
            // [핵심 디버깅 로그] 500 에러를 유발한 진짜 원인을 콘솔에 상세히 출력
            log.error("====================================================");
            log.error("====> [QuizService 500 에러 발생!] pCode: {}", pCode);
            log.error("====> 예외 메시지: {}", e.getMessage());
            log.error("====> 전체 스택 트레이스:", e);
            log.error("====================================================");
            throw e;
        }
    }

    public QuizResultResponse getQuizResultByDate(Integer pCode, LocalDate date) {
        QuizResult quizResult = quizResultRepository.findBypCodeAndDate(pCode, date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 퀴즈 결과를 찾을 수 없습니다. (pCode: " + pCode + ", date: " + date + ")"));

        return QuizResultResponse.from(quizResult, 0);
    }

    /**
     * [수정 2] setId로 피드백 조회 시 feedbackId 대신 setId 기준 메서드 사용
     * QuizFeedbackRepository에 findBySetId(Integer setId) 메서드가 있어야 합니다.
     */
    public List<QuizFeedbackResponse> getQuizFeedback(Integer setId) {
        //  findById(setId) → findBySetId(setId) 로 수정 (feedbackId와 setId 혼동 버그 수정)
        List<QuizFeedback> feedbacks = quizFeedbackRepository.findBySetId(setId);

        if (feedbacks.isEmpty()) {
            throw new IllegalArgumentException("해당 퀴즈 세트의 피드백이 존재하지 않습니다. setId: " + setId);
        }

        List<QuizFeedbackResponse> list = new ArrayList<>();
        for (QuizFeedback feedback : feedbacks) {
            list.add(new QuizFeedbackResponse(
                    feedback.getFeedbackId(),
                    feedback.getSetId(),
                    feedback.getFeedbackContent(),
                    feedback.getCreatedAt() != null ? feedback.getCreatedAt().toString() : null
            ));
        }

        return list;
    }

    public List<QuizResultResponse> getAllQuizResultsByPCode(Integer pCode, LocalDate from, LocalDate to) {
        List<QuizResult> results;

        if (from != null && to != null) {
            results = quizResultRepository.findBypCodeAndDateBetween(pCode, from, to);
        } else {
            results = quizResultRepository.findBypCode(pCode);
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

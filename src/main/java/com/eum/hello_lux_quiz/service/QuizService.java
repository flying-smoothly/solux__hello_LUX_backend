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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class QuizService {

    /** 사진 기반 퀴즈 유형 코드 (prompt.txt의 quiz_category 규칙과 동일) */
    private static final String CATEGORY_PHOTO = "photo";
    /** 사진이 없을 때 강등시킬 유형 코드 */
    private static final String CATEGORY_TEXT = "text";

    /** LLM이 "URL 없음"을 표현할 때 흔히 넣는 값들 (실제 URL이 아니므로 null 취급) */
    private static final Set<String> BLANK_PHOTO_TOKENS = Set.of(
            "없음", "null", "none", "n/a", "na", "-", "미제공", "no image", "없습니다"
    );

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
            log.warn("===> [buildLifeDbContext] pCode: {}에 해당하는 LifeDb 데이터가 없습니다.", pCode);
            return "";
        }

        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("=== 환자 회상 정보 (삶의DB 및 세분화 사건) ===\n");

        // 사진 퀴즈에 실제로 사용할 수 있는 URL 목록 (프롬프트 하단에 화이트리스트로 첨부)
        Set<String> availablePhotoUrls = new LinkedHashSet<>();

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
                // 사진이 있는 사건을 먼저 배치한다.
                // 단순 셔플 후 3개만 자르면 사진 있는 사건이 통째로 탈락해서
                // LLM에게 넘길 이미지 URL이 하나도 없는 상황(= 사진 퀴즈 이미지 깨짐)이 발생한다.
                List<DetailEvent> withPhoto = new ArrayList<>();
                List<DetailEvent> withoutPhoto = new ArrayList<>();
                for (DetailEvent detail : detailList) {
                    if (normalizePhotoUrl(detail.getPhotoUrl()) != null) {
                        withPhoto.add(detail);
                    } else {
                        withoutPhoto.add(detail);
                    }
                }
                Collections.shuffle(withPhoto);
                Collections.shuffle(withoutPhoto);

                List<DetailEvent> selectedDetails = new ArrayList<>(withPhoto);
                selectedDetails.addAll(withoutPhoto);
                selectedDetails = selectedDetails.stream().limit(3).toList();

                contextBuilder.append("  [연관 세부 사건 (사진 보유 사건 우선, 최대 3개)]\n");
                for (DetailEvent detail : selectedDetails) {
                    String photoUrl = normalizePhotoUrl(detail.getPhotoUrl());
                    if (photoUrl != null) {
                        availablePhotoUrls.add(photoUrl);
                    }
                    contextBuilder.append(String.format("  * 내용: %s / 사진URL: %s / 감정: %s\n",
                            detail.getEvent(),
                            photoUrl != null ? photoUrl : "없음",
                            detail.getCategory()
                    ));
                }
            }
            contextBuilder.append("-----------------------\n");
        }

        // LLM이 URL을 지어내지 못하도록, 사용 가능한 URL을 명시적인 화이트리스트로 다시 알려준다.
        contextBuilder.append("\n[사용 가능한 이미지 URL 화이트리스트]\n");
        if (availablePhotoUrls.isEmpty()) {
            contextBuilder.append("- (없음) 이 환자는 등록된 사진이 없습니다. "
                    + "quiz_photo 는 모든 문항에서 반드시 null 이어야 하고, "
                    + "quiz_category 에 \"photo\" 를 사용해서는 안 됩니다.\n");
        } else {
            for (String url : availablePhotoUrls) {
                contextBuilder.append("- ").append(url).append('\n');
            }
            contextBuilder.append("※ quiz_photo 값은 위 목록의 문자열 중 하나와 완전히 일치해야 합니다. "
                    + "목록에 없는 URL은 절대 사용하지 마세요.\n");
        }

        return contextBuilder.toString();
    }

    /**
     * 사진 URL 정규화.
     *
     * <p>다음은 모두 "사진 없음"(null)으로 취급한다.
     * <ul>
     *   <li>공백 문자열</li>
     *   <li>"없음"/"null" 같은 placeholder</li>
     *   <li>절대 URL이 아닌 값 — 예: R2_PUBLIC_URL 미설정 시 저장된 "/patients/1/xxx.jpg".
     *       브라우저가 프론트엔드 origin(localhost:5173) 기준으로 해석해 404가 나므로
     *       사진이 있는 것처럼 내려보내면 안 된다.</li>
     * </ul>
     */
    private String normalizePhotoUrl(String photoUrl) {
        if (photoUrl == null) {
            return null;
        }
        String trimmed = photoUrl.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (BLANK_PHOTO_TOKENS.contains(trimmed.toLowerCase(Locale.ROOT))) {
            return null;
        }
        if (!isAbsoluteHttpUrl(trimmed)) {
            log.warn("===> [normalizePhotoUrl] 절대 URL이 아니라 사용할 수 없는 사진 주소입니다. "
                    + "R2_PUBLIC_URL 설정을 확인하세요. (값: {})", trimmed);
            return null;
        }
        return trimmed;
    }

    /**
     * 브라우저가 그대로 로드할 수 있는 절대 http(s) URL 인지 확인한다.
     */
    private boolean isAbsoluteHttpUrl(String url) {
        String lower = url.toLowerCase(Locale.ROOT);
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    /**
     * 이 환자가 실제로 보유한 사진 URL 집합. LLM 응답 검증용 화이트리스트로 사용한다.
     */
    private Set<String> collectPatientPhotoUrls(Integer pCode) {
        Set<String> urls = new LinkedHashSet<>();
        for (LifeDb memory : lifeDbRepository.findByPCode(pCode)) {
            List<DetailEvent> detailList = detailRepository.findByMemoryId(memory.getMemoryId());
            if (detailList == null) {
                continue;
            }
            for (DetailEvent detail : detailList) {
                String url = normalizePhotoUrl(detail.getPhotoUrl());
                if (url != null) {
                    urls.add(url);
                }
            }
        }
        return urls;
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
        Optional<QuizResult> todayResult = quizResultRepository.findByPCodeAndDate(pCode, LocalDate.now());

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
            dto.setQuizComment(item.getQuizComment());
            dto.setAnswer(item.getAnswer());

            // 이미 저장된 세트에도 "없음"/빈 문자열 같은 값이 남아 있을 수 있다.
            // 그대로 내려보내면 프론트에서 <img src=""> 로 렌더링되어 깨진 이미지가 보인다.
            String storedPhoto = normalizePhotoUrl(item.getQuizPhoto());
            String storedCategory = item.getQuizCategory();
            if (CATEGORY_PHOTO.equalsIgnoreCase(storedCategory) && storedPhoto == null) {
                log.warn("===> [getOrCreateTodayQuiz] setId: {}, quizNum: {} 사진이 없는 photo 문항입니다. text 로 내려보냅니다.",
                        savedQuizSet.getSetId(), item.getQuizNum());
                storedCategory = CATEGORY_TEXT;
            }
            dto.setQuizCategory(storedCategory);
            dto.setQuizPhoto(storedPhoto);

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
                caculate
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

            log.info("===> [QuizGeneratorService 호출 시작] patientStatus: {}", patientStatus);

            // LLM 퀴즈 생성 호출
            List<GeneratedQuizItemDto> generatedItems = quizGeneratorService.generateQuizSet(
                    patientStatus, profile, finalContext + todayContext, timeOrientationInstruction
            );

            log.info("===> [LLM 응답 받아옴] 생성된 문항 개수: {}", generatedItems != null ? generatedItems.size() : 0);

            // 이 환자가 실제로 보유한 사진 URL (LLM 응답 검증용)
            Set<String> allowedPhotoUrls = collectPatientPhotoUrls(pCode);

            // 대체가 필요할 때는 프롬프트에 실제로 실린 사진을 우선한다.
            // (LLM이 못 본 사진으로 바꾸면 지문과 사진이 어긋난다)
            Set<String> contextPhotoUrls = allowedPhotoUrls.stream()
                    .filter(finalContext::contains)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            Set<String> fallbackPhotoUrls = contextPhotoUrls.isEmpty() ? allowedPhotoUrls : contextPhotoUrls;

            log.info("===> [createQuizSet] pCode: {} 보유 사진 URL: {}개 (프롬프트 포함: {}개)",
                    pCode, allowedPhotoUrls.size(), contextPhotoUrls.size());

            // [보완] LLM 응답의 Null Pointer 방어
            if (generatedItems != null) {
                int quizNumCounter = 1;
                for (GeneratedQuizItemDto dto : generatedItems) {
                    String category = (dto.getQuizCategory() != null) ? dto.getQuizCategory() : CATEGORY_TEXT;
                    String photoUrl = resolveQuizPhoto(
                            dto.getQuizPhoto(), category, allowedPhotoUrls, fallbackPhotoUrls, quizNumCounter);

                    // 사진 없는 "photo" 문항은 프론트에서 깨진 이미지로 보이므로 text 로 강등한다.
                    if (CATEGORY_PHOTO.equalsIgnoreCase(category) && photoUrl == null) {
                        log.warn("===> [createQuizSet] quizNum: {} 사진 URL이 없어 photo -> text 로 강등합니다.", quizNumCounter);
                        category = CATEGORY_TEXT;
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
                            photoUrl,
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

    /**
     * LLM이 반환한 quiz_photo 를 검증한다.
     *
     * <p>프롬프트로 "실제 URL만 복사하라"고 지시해도 LLM은 종종 URL을 지어내거나
     * ("https://example.com/..."), "없음" 같은 문자열을 넣는다. 그대로 저장하면
     * 프론트에서 이미지가 깨진 채로 노출되므로 화이트리스트로 한번 더 거른다.
     *
     * @param allowedPhotoUrls  환자가 보유한 전체 사진 URL (통과 기준)
     * @param fallbackPhotoUrls 대체가 필요할 때 고를 후보 (프롬프트에 실린 사진 우선)
     * @return 저장해도 되는 URL, 사용할 수 없으면 null
     */
    private String resolveQuizPhoto(String rawPhotoUrl, String category,
            Set<String> allowedPhotoUrls, Set<String> fallbackPhotoUrls, int quizNum) {
        String photoUrl = normalizePhotoUrl(rawPhotoUrl);

        // 사진 퀴즈가 아니면 URL을 붙이지 않는다.
        if (!CATEGORY_PHOTO.equalsIgnoreCase(category)) {
            return null;
        }

        if (photoUrl != null && allowedPhotoUrls.contains(photoUrl)) {
            return photoUrl;
        }

        if (fallbackPhotoUrls.isEmpty()) {
            log.warn("===> [resolveQuizPhoto] quizNum: {} 환자에게 등록된 사진이 없습니다. quiz_photo 를 비웁니다. (LLM 응답: {})",
                    quizNum, rawPhotoUrl);
            return null;
        }

        // LLM이 지어낸 URL -> 환자의 실제 사진 중 하나로 대체한다.
        String fallback = fallbackPhotoUrls.iterator().next();
        log.warn("===> [resolveQuizPhoto] quizNum: {} LLM이 허용되지 않은 URL을 반환했습니다. 실제 사진으로 대체합니다. (응답: {} -> 대체: {})",
                quizNum, rawPhotoUrl, fallback);
        return fallback;
    }

    public QuizResultResponse getQuizResultByDate(Integer pCode, LocalDate date) {
        QuizResult quizResult = quizResultRepository.findByPCodeAndDate(pCode, date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 퀴즈 결과를 찾을 수 없습니다. (pCode: " + pCode + ", date: " + date + ")"));

        return QuizResultResponse.from(quizResult, 0);
    }

    /**
     * [수정 2] setId로 피드백 조회 시 feedbackId 대신 setId 기준 메서드 사용
     * QuizFeedbackRepository에 findBySetId(Integer setId) 메서드가 있어야 합니다.
     */
    public List<QuizFeedbackResponse> getQuizFeedback(Integer setId) {
        // ✅ findById(setId) → findBySetId(setId) 로 수정 (feedbackId와 setId 혼동 버그 수정)
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

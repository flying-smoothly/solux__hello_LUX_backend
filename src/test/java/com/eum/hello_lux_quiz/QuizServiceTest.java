package com.eum.hello_lux_quiz;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.eum.hello_lux_quiz.domain.DetailEvent;
import com.eum.hello_lux_quiz.domain.LifeDb;
import com.eum.hello_lux_quiz.domain.PatientProfile;
import com.eum.hello_lux_quiz.domain.QuizItem;
import com.eum.hello_lux_quiz.domain.QuizResult;
import com.eum.hello_lux_quiz.domain.QuizSet;
import com.eum.hello_lux_quiz.dto.GeneratedQuizItemDto;
import com.eum.hello_lux_quiz.dto.QuizAnswerResponse;
import com.eum.hello_lux_quiz.repository.DetailRepository;
import com.eum.hello_lux_quiz.repository.LifeDbRepository;
import com.eum.hello_lux_quiz.repository.PatientProfileRepository;
import com.eum.hello_lux_quiz.repository.QuizItemRepository;
import com.eum.hello_lux_quiz.repository.QuizResultRepository;
import com.eum.hello_lux_quiz.repository.QuizSetRepository;
import com.eum.hello_lux_quiz.service.QuizGeneratorService;
import com.eum.hello_lux_quiz.service.QuizScoringService;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class QuizServiceTest {

    @Autowired
    private QuizGeneratorService quizGeneratorService;

    @Autowired
    private QuizScoringService quizScoringService;

    // 💡 DB에서 직접 조회하는 대신 MockitoBean으로 가로챕니다.
    @MockitoBean
    private PatientProfileRepository patientProfileRepository;

    @MockitoBean
    private LifeDbRepository lifeDbRepository;

    @MockitoBean
    private DetailRepository detailRepository;

    @Autowired
    private QuizSetRepository quizSetRepository;

    @Autowired
    private QuizItemRepository quizItemRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @BeforeEach
    void setUpMockData() {
        Integer targetPCode = 1;

        // 1. PatientProfile Mocking (가짜 프로필 리턴)
        PatientProfile mockProfile = Mockito.mock(PatientProfile.class);
        given(mockProfile.getPatientStatus()).willReturn("유지");
        given(patientProfileRepository.findByPCode(targetPCode)).willReturn(Optional.of(mockProfile));

        // 2. LifeDb Mocking (가짜 LifeDb 리턴)
        LifeDb mockLifeDb = Mockito.mock(LifeDb.class);
        given(mockLifeDb.getMemoryId()).willReturn(100);
        given(mockLifeDb.getHometown()).willReturn("서울");
        given(mockLifeDb.getJob()).willReturn("선생님");
        given(mockLifeDb.getFamily()).willReturn("아내, 아들");
        given(mockLifeDb.getLikes()).willReturn("산책, 화초 가꾸기");
        given(mockLifeDb.getPlace()).willReturn("남산공원");
        given(mockLifeDb.getTitle()).willReturn("즐거운 나의 집");

        given(lifeDbRepository.findByPCode(targetPCode)).willReturn(List.of(mockLifeDb));

        // 3. DetailEvent Mocking (가짜 세부 사건 리턴)
        DetailEvent mockDetail = Mockito.mock(DetailEvent.class);
        given(mockDetail.getEvent()).willReturn("봄날 남산공원 소풍");
        given(mockDetail.getPhotoUrl()).willReturn("https://example.com/photo.jpg");
        given(mockDetail.getCategory()).willReturn("행복");

        given(detailRepository.findByMemoryId(100)).willReturn(List.of(mockDetail));
    }

    @Test
    @DisplayName("퀴즈 생성 -> 임의 답안 채점 -> DB 저장 및 결과 출력 전체 테스트")
    void fullQuizFlowTest() throws InterruptedException {
        // 1. given: p_code = 1 환자 데이터 준비
        Integer targetPCode = 1;

        PatientProfile profile = patientProfileRepository.findByPCode(targetPCode)
                .orElseThrow(() -> new IllegalArgumentException("p_code=" + targetPCode + " 환자 프로필을 찾을 수 없습니다."));

        List<LifeDb> lifeDbList = lifeDbRepository.findByPCode(targetPCode);
        if (lifeDbList.isEmpty()) {
            throw new IllegalArgumentException("p_code=" + targetPCode + " 의 삶의DB 데이터가 없습니다.");
        }

        // Context 조립 (삶의DB + 세분화 사건 랜덤 3개)
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("=== 환자 회상 정보 (삶의DB 및 세분화 사건) ===\n");
        for (LifeDb memory : lifeDbList) {
            contextBuilder.append(String.format("""
                - 고향: %s / 직업: %s / 가족: %s
                - 좋아하는 것: %s / 주요 장소: %s / 제목: %s
                """,
                    memory.getHometown(),
                    memory.getJob(),
                    memory.getFamily(),
                    memory.getLikes(),
                    memory.getPlace(),
                    memory.getTitle()
            ));

            // 세분화 목록 조회 후 랜덤 3개 추출
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

        String patientStatus = profile.getPatientStatus() != null ? profile.getPatientStatus() : "유지";
        String lifeDbContext = contextBuilder.toString();

        System.out.println("⏳ Rate Limit 방지를 위해 3초간 대기합니다...");
        Thread.sleep(3000);

        // 2. when (Step 1): AI 퀴즈 생성
        List<GeneratedQuizItemDto> generatedDtos = quizGeneratorService.generateQuizSet(patientStatus, profile, lifeDbContext, "");

        // (1) 퀴즈 세트 DB 저장
        QuizSet quizSet = new QuizSet(
                targetPCode,
                LocalTime.now(),
                LocalTime.now(),
                0
        );
        QuizSet savedQuizSet = quizSetRepository.save(quizSet);

        // (2) 퀴즈 아이템 DB 저장 및 임의 답안 채점 진행
        int correctCount = 0;
        int quizNumCounter = 1; // 👈 quizNum 지정을 위한 카운터 변수 추가

        System.out.println("\n==================================================");
        System.out.println(" 📝 [START] 생성된 퀴즈 풀이 및 LLM 채점 시작");
        System.out.println("==================================================");

        for (GeneratedQuizItemDto dto : generatedDtos) {
            // options와 hints를 DB 저장을 위한 문자열 형태로 변환
            String optionsString = (dto.getOptions() != null && !dto.getOptions().isEmpty())
                    ? String.join(", ", dto.getOptions())
                    : null;

            String hintsString = (dto.getHints() != null && !dto.getHints().isEmpty())
                    ? String.join(", ", dto.getHints())
                    : null;

            QuizItem quizItem = new QuizItem(
                    savedQuizSet.getSetId(),
                    targetPCode,
                    quizNumCounter++, // 👈 3번째 인자에 Integer 타입의 quizNumCounter 전달
                    dto.getQuizCategory() != null ? dto.getQuizCategory() : "text",
                    dto.getLevel(),
                    dto.getQuizComment(),
                    dto.getQuizPhoto(),
                    dto.getAnswer(),
                    optionsString,
                    hintsString
            );
            quizItemRepository.save(quizItem);

            String dummyUserAnswer = (dto.getQuizNum() % 2 == 0) ? dto.getAnswer() : "잘 모르겠습니다";

            QuizAnswerResponse scoreResult = quizScoringService.scoreAnswer(
                    dto.getQuizComment(),
                    dto.getAnswer(),
                    dummyUserAnswer
            );

            if (scoreResult.isCorrect()) {
                correctCount++;
            }

            System.out.println(String.format("""
                [Q%d - Level %d (%s)]
                - 질문: %s
                - 보기: %s
                - 힌트: %s
                - 정답: %s
                - 제출 답안: %s
                - 채점 결과: %s (%s)
                --------------------------------------------------""",
                    dto.getQuizNum(),
                    dto.getLevel(),
                    dto.getQuizCategory(),
                    dto.getQuizComment(),
                    (dto.getOptions() != null && !dto.getOptions().isEmpty()) ? dto.getOptions() : "[]",
                    (dto.getHints() != null && !dto.getHints().isEmpty()) ? dto.getHints() : "[]",
                    dto.getAnswer(),
                    dummyUserAnswer,
                    scoreResult.isCorrect() ? "⭕ 정답" : "❌ 오답",
                    scoreResult.getFeedback()
            ));
        }

        // 3. when (Step 2): 최종 퀴즈 결과(QuizResult) DB 저장
        QuizResult quizResult = new QuizResult(
                savedQuizSet.getSetId(),
                targetPCode,
                LocalDate.now(),
                generatedDtos.size(),
                correctCount,
                0,
                "N"
        );
        quizResultRepository.save(quizResult);

        // 4. then: 검증 및 종합 결과 터미널 출력
        assertThat(generatedDtos).hasSize(7);

        System.out.println("\n==================================================");
        System.out.println(" 🎉 [COMPLETED] 퀴즈 결과 DB 저장 및 테스트 완료!");
        System.out.println(" - 저장된 Set ID: " + savedQuizSet.getSetId());
        System.out.println(" - 총 문제 수: " + generatedDtos.size());
        System.out.println(" - 맞힌 개수: " + correctCount + " / " + generatedDtos.size());
        System.out.println("==================================================\n");
    }
}

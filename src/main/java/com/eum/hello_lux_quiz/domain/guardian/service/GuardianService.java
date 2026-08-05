package com.eum.hello_lux_quiz.domain.guardian.service;

import com.eum.hello_lux_quiz.domain.guardian.dto.DashboardResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.LinkResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.LinkedPatientResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.MemoCreateResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.MemoRequest;
import com.eum.hello_lux_quiz.domain.guardian.dto.MemoResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.TrendResponse;
import com.eum.hello_lux_quiz.domain.guardian.entity.Guardian;
import com.eum.hello_lux_quiz.domain.guardian.entity.StatusMemo;
import com.eum.hello_lux_quiz.domain.guardian.repository.GuardianRepository;
import com.eum.hello_lux_quiz.domain.guardian.repository.StatusMemoRepository;
import com.eum.hello_lux_quiz.domain.patient.service.PatientService;
import com.eum.hello_lux_quiz.global.exception.CustomException;
import com.eum.hello_lux_quiz.global.exception.ErrorCode;
import com.eum.hello_lux_quiz.global.quiz.QuizStatsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuardianService {

    private final GuardianRepository guardianRepository;
    private final StatusMemoRepository statusMemoRepository;
    private final PatientService patientService;
    private final QuizStatsPort quizStatsPort;

    /**
     * 보호자-환자 연동. 환자 존재를 확인하고 중복 연동을 방지한다.
     */
    @Transactional
    public LinkResponse link(String userId, String patientCode) {
        Integer pCode = patientService.resolvePCode(patientCode);


        if (guardianRepository.existsByPCodeAndUserId(pCode, userId)) {
            throw new CustomException(ErrorCode.ALREADY_LINKED);
        }

        guardianRepository.save(Guardian.builder()
                .pCode(pCode)
                .userId(userId)
                .build());

        return new LinkResponse("연동 완료", patientService.getPatientName(pCode));
    }

    /**
     * 연동된 환자 목록 조회. last_score 는 퀴즈 모듈 연동 시 채워진다.
     */
    public List<LinkedPatientResponse> getLinkedPatients(String email) {
        return guardianRepository.findAllByUserId(email).stream()
                .map(guardian -> {
                    Integer pCode = guardian.getPCode();
                    Integer lastScore = quizStatsPort.getLastScore(pCode);
                    return new LinkedPatientResponse(
                            patientService.getPatientCode(pCode),
                            patientService.getPatientName(pCode),
                            lastScore);
                })
                .toList();
    }

    /**
     * 환자 요약 대시보드. 퀴즈 결과 집계에 의존한다.
     */
    public DashboardResponse getDashboard(String email, Integer pCode) {
        validateLinked(email, pCode);
        return new DashboardResponse(
                quizStatsPort.getAverageScore(pCode),
                quizStatsPort.getTrend(pCode),
                quizStatsPort.getLastQuizDate(pCode)
        );
    }

    /**
     * 변화 추이 조회. 퀴즈 결과 집계에 의존한다.
     */
    public TrendResponse getTrend(String email, Integer pCode, String period) {
        validateLinked(email, pCode);
        return new TrendResponse(
                quizStatsPort.getTrendLabels(pCode, period),
                quizStatsPort.getTrendScores(pCode, period)
        );
    }

    /**
     * 환자 상태 메모 작성.
     */
    @Transactional
    public MemoCreateResponse createMemo(String email, Integer pCode, MemoRequest request) {
        validateLinked(email, pCode);
        StatusMemo memo = statusMemoRepository.save(StatusMemo.builder()
                .pCode(pCode)
                .userId(email)
                .recordDate(resolveRecordDate(request.recordDate()))
                .healthStatus(request.healthStatus())
                .sleepStatus(request.sleepStatus())
                .mealStatus(request.mealStatus())
                .painStatus(request.painStatus())
                .moodStatus(request.moodStatus())
                .behaviors(joinBehaviors(request.behaviors()))
                .needReferral(Boolean.TRUE.equals(request.needReferral()))
                .content(request.content())
                .build());
        return MemoCreateResponse.from(memo);
    }

    /**
     * 상태 메모 목록 조회(최신순).
     */
    public List<MemoResponse> getMemos(String email, Integer pCode) {
        validateLinked(email, pCode);
        return statusMemoRepository.findAllByPCodeOrderByRecordDateDesc(pCode).stream()
                .map(MemoResponse::from)
                .toList();
    }

       /**
     * 상태 기록 수정. 본인이 작성한 기록만 수정 가능.
     */
    @Transactional
    public MemoResponse updateMemo(String email, Integer pCode, Long memoId, MemoRequest request) {
        StatusMemo memo = getOwnedMemo(email, pCode, memoId);
        memo.update(
                resolveRecordDate(request.recordDate()),
                request.healthStatus(),
                request.sleepStatus(),
                request.mealStatus(),
                request.painStatus(),
                request.moodStatus(),
                joinBehaviors(request.behaviors()),
                Boolean.TRUE.equals(request.needReferral()),
                request.content());
        return MemoResponse.from(memo);
    }

    /**
     * 상태 기록 삭제. 본인이 작성한 기록만 삭제 가능.
     */
    @Transactional
    public void deleteMemo(String email, Integer pCode, Long memoId) {
        StatusMemo memo = getOwnedMemo(email, pCode, memoId);
        statusMemoRepository.delete(memo);
    }

    private StatusMemo getOwnedMemo(String email, Integer pCode, Long memoId) {
        validateLinked(email, pCode);
        StatusMemo memo = statusMemoRepository.findById(memoId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMO_NOT_FOUND));
        if (!memo.getPCode().equals(pCode) || !memo.getUserId().equals(email)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        return memo;
    }

    private LocalDate resolveRecordDate(LocalDate recordDate) {
        return (recordDate != null) ? recordDate : LocalDate.now();
    }

    private String joinBehaviors(List<String> behaviors) {
        if (behaviors == null || behaviors.isEmpty()) {
            return null;
        }
        return String.join(",", behaviors);
    }

    private void validateLinked(String email, Integer pCode) {
        patientService.validateExists(pCode);
        if (!guardianRepository.existsByPCodeAndUserId(pCode, email)) {
            throw new CustomException(ErrorCode.LINK_NOT_FOUND);
        }
    }
}

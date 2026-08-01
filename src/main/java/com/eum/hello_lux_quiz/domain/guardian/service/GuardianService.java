package com.eum.hello_lux_quiz.domain.guardian.service;

import com.eum.hello_lux_quiz.domain.guardian.dto.DashboardResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.LinkResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.LinkedPatientResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.MemoCreateResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.MemoRequest;
import com.eum.hello_lux_quiz.domain.guardian.dto.MemoResponse;
import com.eum.hello_lux_quiz.domain.guardian.dto.PatientInfoRequest;
import com.eum.hello_lux_quiz.domain.guardian.dto.TrendResponse;
import com.eum.hello_lux_quiz.domain.guardian.entity.Guardian;
import com.eum.hello_lux_quiz.domain.guardian.entity.StatusMemo;
import com.eum.hello_lux_quiz.domain.guardian.repository.GuardianRepository;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientInfoResponse;
import com.eum.hello_lux_quiz.domain.patient.entity.Patient;
import com.eum.hello_lux_quiz.domain.guardian.repository.StatusMemoRepository;
import com.eum.hello_lux_quiz.domain.patient.service.PatientService;
import com.eum.hello_lux_quiz.global.exception.CustomException;
import com.eum.hello_lux_quiz.global.exception.ErrorCode;
import com.eum.hello_lux_quiz.global.quiz.QuizStatsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public LinkResponse link(String email, Integer pCode) {
        patientService.validateExists(pCode);

        if (guardianRepository.existsByPCodeAndUserEmail(pCode, email)) {
            throw new CustomException(ErrorCode.ALREADY_LINKED);
        }

        guardianRepository.save(Guardian.builder()
                .pCode(pCode)
                .userEmail(email)
                .build());

        return new LinkResponse("연동 완료", patientService.getPatientName(pCode));
    }

    /**
     * 연동된 환자 목록 조회. last_score 는 퀴즈 모듈 연동 시 채워진다.
     */
    public List<LinkedPatientResponse> getLinkedPatients(String email) {
        return guardianRepository.findAllByUserEmail(email).stream()
                .map(guardian -> {
                    Integer pCode = guardian.getPCode();
                    Integer lastScore = quizStatsPort.getLastScore(pCode);
                    return new LinkedPatientResponse(pCode, patientService.getPatientName(pCode), lastScore);
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
     * 보호자가 연동된 환자의 정보를 대신 입력/수정한다.
     * (성별/진단명/성격/말투 — 전달된 필드만 반영)
     */
    @Transactional
    public PatientInfoResponse updatePatientInfo(String email, Integer pCode, PatientInfoRequest request) {
        validateLinked(email, pCode);
        Patient patient = patientService.getPatient(pCode);
        patient.updateInfo(request.gender(), request.diagnosis(),
                request.personality(), request.speechStyle());
        return patientService.getInfo(pCode);
    }


    /**
     * 환자 상태 메모 작성.
     */
    @Transactional
    public MemoCreateResponse createMemo(String email, Integer pCode, MemoRequest request) {
        validateLinked(email, pCode);
        StatusMemo memo = statusMemoRepository.save(StatusMemo.builder()
                .pCode(pCode)
                .userEmail(email)
                .content(request.content())
                .build());
        return MemoCreateResponse.from(memo);
    }

    /**
     * 상태 메모 목록 조회(최신순).
     */
    public List<MemoResponse> getMemos(String email, Integer pCode) {
        validateLinked(email, pCode);
        return statusMemoRepository.findAllByPCodeOrderByCreatedAtDesc(pCode).stream()
                .map(MemoResponse::from)
                .toList();
    }

    private void validateLinked(String email, Integer pCode) {
        patientService.validateExists(pCode);
        if (!guardianRepository.existsByPCodeAndUserEmail(pCode, email)) {
            throw new CustomException(ErrorCode.LINK_NOT_FOUND);
        }
    }
}

package com.eum.hello_lux_quiz.domain.doctor.service;

import com.eum.hello_lux_quiz.domain.doctor.dto.DoctorPatientResponse;
import com.eum.hello_lux_quiz.domain.doctor.dto.LevelRequest;
import com.eum.hello_lux_quiz.domain.doctor.dto.ReportResponse;
import com.eum.hello_lux_quiz.domain.doctor.dto.ReportUpdateRequest;
import com.eum.hello_lux_quiz.domain.doctor.entity.DoctorPatient;
import com.eum.hello_lux_quiz.domain.doctor.entity.DoctorReport;
import com.eum.hello_lux_quiz.domain.doctor.entity.PatientQuizLevel;
import com.eum.hello_lux_quiz.domain.doctor.repository.DoctorPatientRepository;
import com.eum.hello_lux_quiz.domain.doctor.repository.DoctorReportRepository;
import com.eum.hello_lux_quiz.domain.doctor.repository.PatientQuizLevelRepository;
import com.eum.hello_lux_quiz.domain.patient.service.PatientService;
import com.eum.hello_lux_quiz.global.exception.CustomException;
import com.eum.hello_lux_quiz.global.exception.ErrorCode;
import com.eum.hello_lux_quiz.global.quiz.QuizStatsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorService {

    private final DoctorPatientRepository doctorPatientRepository;
    private final DoctorReportRepository doctorReportRepository;
    private final PatientQuizLevelRepository patientQuizLevelRepository;
    private final PatientService patientService;
    private final QuizStatsPort quizStatsPort;

    /**
     * 의사-환자 연동.
     */
    @Transactional
    public void link(String email, Integer pCode) {
        patientService.validateExists(pCode);
        if (doctorPatientRepository.existsByPCodeAndUserEmail(pCode, email)) {
            throw new CustomException(ErrorCode.ALREADY_LINKED);
        }
        doctorPatientRepository.save(DoctorPatient.builder()
                .pCode(pCode)
                .userEmail(email)
                .build());
    }

    /**
     * 담당 환자 목록 조회.
     */
    public List<DoctorPatientResponse> getPatients(String email) {
        return doctorPatientRepository.findAllByUserEmail(email).stream()
                .map(dp -> DoctorPatientResponse.from(
                        patientService.getPatient(dp.getPCode()),
                        patientService.getPatientName(dp.getPCode())))
                .toList();
    }

    /**
     * 진료 참고 리포트 조회. 퀴즈 결과 집계에 의존한다.
     */
    public ReportResponse getReport(String email, Integer pCode) {
        validateLinked(email, pCode);
        List<String> feedbackList = Collections.emptyList(); // 퀴즈 피드백 모듈 연동 시 채워진다.
        return new ReportResponse(
                quizStatsPort.getAverageScore(pCode),
                quizStatsPort.getTrend(pCode),
                feedbackList
        );
    }

    /**
     * 난이도 조절. (p_code, quiz_type) 별 레벨을 upsert 한다.
     */
    @Transactional
    public void updateLevel(String email, Integer pCode, LevelRequest request) {
        validateLinked(email, pCode);
        patientQuizLevelRepository.findByPCodeAndQuizType(pCode, request.quizType())
                .ifPresentOrElse(
                        level -> level.updateLevel(request.level()),
                        () -> patientQuizLevelRepository.save(PatientQuizLevel.builder()
                                .pCode(pCode)
                                .quizType(request.quizType())
                                .level(request.level())
                                .build())
                );
    }

    /**
     * 진료 리포트 작성/수정. (의사, 환자) 당 하나의 리포트를 upsert 한다.
     */
    @Transactional
    public void saveReport(String email, Integer pCode, ReportUpdateRequest request) {
        validateLinked(email, pCode);
        doctorReportRepository.findByPCodeAndUserEmail(pCode, email)
                .ifPresentOrElse(
                        report -> report.updateReport(request.report()),
                        () -> doctorReportRepository.save(DoctorReport.builder()
                                .pCode(pCode)
                                .userEmail(email)
                                .report(request.report())
                                .build())
                );
    }

    private void validateLinked(String email, Integer pCode) {
        patientService.validateExists(pCode);
        if (!doctorPatientRepository.existsByPCodeAndUserEmail(pCode, email)) {
            throw new CustomException(ErrorCode.LINK_NOT_FOUND);
        }
    }
}

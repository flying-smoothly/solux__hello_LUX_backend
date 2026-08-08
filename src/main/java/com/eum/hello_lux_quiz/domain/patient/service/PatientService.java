package com.eum.hello_lux_quiz.domain.patient.service;

import com.eum.hello_lux_quiz.domain.member.entity.Member;
import com.eum.hello_lux_quiz.domain.member.repository.MemberRepository;
import com.eum.hello_lux_quiz.domain.patient.dto.DailyStatusRequest;
import com.eum.hello_lux_quiz.domain.patient.dto.DailyStatusResponse;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientInfoResponse;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientMeResponse;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientRegisterRequest;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientRegisterResponse;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientUpdateRequest;
import com.eum.hello_lux_quiz.domain.patient.entity.Patient;
import com.eum.hello_lux_quiz.domain.patient.entity.PatientDailyStatus;
import com.eum.hello_lux_quiz.domain.patient.repository.PatientDailyStatusRepository;
import com.eum.hello_lux_quiz.domain.patient.repository.PatientRepository;
import com.eum.hello_lux_quiz.global.exception.CustomException;
import com.eum.hello_lux_quiz.global.exception.ErrorCode;
import com.eum.hello_lux_quiz.service.PatientProfileProvisioner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

// quiz_llm 의 service.PatientService 와 빈 이름이 겹치지 않도록 명시적 이름 지정
@Service("authPatientService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final MemberRepository memberRepository;
    private final PatientDailyStatusRepository patientDailyStatusRepository;
    private final PatientProfileProvisioner patientProfileProvisioner;

    /**
     * 환자 기본 정보 등록. 환자 코드(p_code)를 발급하며 상세 정보를 함께 저장한다.
     * 이름/생년월일은 회원(Member) 에서 관리하므로 저장하지 않는다.
     */
    @Transactional
    public PatientRegisterResponse register(String email, PatientRegisterRequest request) {
        if (!memberRepository.existsByUserId(email)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }


        if (patientRepository.existsByUserId(email)) {
            throw new CustomException(ErrorCode.PATIENT_ALREADY_REGISTERED);
        }

        Patient patient = patientRepository.save(Patient.builder()
                .userId(email)
                .patientCode(generatePatientCode())
                .gender(request.gender())
                .diagnosis(request.diagnosis())
                .cognitiveSupportLevel(request.cognitiveSupportLevel())
                .guardianCompanion(request.guardianCompanion())
                .personality(request.personality())
                .speechStyle(request.speechStyle())
                .build());
                
        patientProfileProvisioner.createFrom(patient);
        return new PatientRegisterResponse(patient.getPCode(), patient.getPatientCode(), "환자 등록 완료");
    }

    /** 보호자·의사 연동용 6자리 영문+숫자 코드를 중복되지 않게 발급한다(혼동되는 0/O/1/I 제외). */
    private String generatePatientCode() {
        final String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        final java.security.SecureRandom random = new java.security.SecureRandom();
        for (int attempt = 0; attempt < 20; attempt++) {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
            }
            String code = sb.toString();
            if (!patientRepository.existsByPatientCode(code)) {
                return code;
            }
        }
        throw new CustomException(ErrorCode.PATIENT_CODE_GENERATION_FAILED);
    }

    /**
     * 연동용 코드(patient_code)로 내부 환자 코드(p_code)를 조회한다. 보호자·의사 연동에서 사용.
     */
    public Integer resolvePCode(String patientCode) {
        return patientRepository.findByPatientCode(patientCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PATIENT_NOT_FOUND))
                .getPCode();
    }

    /**
     * 환자 정보 조회. 환자/보호자/의사 공통.
     * 이름/생년월일은 회원(Member) 정보에서 조회한다.
     */
    public PatientInfoResponse getInfo(Integer pCode) {
        Patient patient = getPatient(pCode);
        Member member = getPatientMember(patient);
        return PatientInfoResponse.from(patient, member.getName(), member.getBirthDate());
    }
    /**
     * 로그인한 환자 본인의 정보 조회. 내부 식별자(internal_code)와 6자리 연동 코드를 함께 돌려준다.
     * 등록 응답을 놓쳤거나 다른 기기에서 재로그인한 경우 이 API 로 다시 조회한다.
     */
    public PatientMeResponse getMe(String userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PATIENT_NOT_FOUND));
        return PatientMeResponse.from(patient, getPatientName(patient.getPCode()));
    }

    /**
     * 환자 정보 수정(성격/말투). 본인만 가능.
     */
    @Transactional
    public void update(String email, Integer pCode, PatientUpdateRequest request) {
        validateOwner(email, pCode);
        Patient patient = getPatient(pCode);
        patient.update(request.personality(), request.speechStyle());
    }

    /**
     * 환자 코드 조회. 본인만 가능.
     */
    public Integer getCode(String email, Integer pCode) {
        validateOwner(email, pCode);
        return pCode;
    }

       // ===== 환자 일일 상태(건강 체크) 입력 =====

    /**
     * 환자 본인이 오늘의 상태(건강/수면/식사/통증/기분/인지 변화)를 입력한다.
     * 하루 1건을 유지하며, 같은 날 재입력 시 기존 기록을 갱신한다.
     */
    @Transactional
    public DailyStatusResponse saveDailyStatus(String email, Integer pCode, DailyStatusRequest request) {
        validateOwner(email, pCode);

        LocalDate today = LocalDate.now();
        String cognitiveChanges = joinCognitiveChanges(request.cognitiveChanges());

        PatientDailyStatus status = patientDailyStatusRepository
                .findByPCodeAndRecordDate(pCode, today)
                .map(existing -> {
                    existing.update(request.healthCondition(), request.sleepStatus(),
                            request.mealStatus(), request.painStatus(),
                            request.moodStatus(), cognitiveChanges);
                    return existing;
                })
                .orElseGet(() -> patientDailyStatusRepository.save(PatientDailyStatus.builder()
                        .pCode(pCode)
                        .recordDate(today)
                        .healthCondition(request.healthCondition())
                        .sleepStatus(request.sleepStatus())
                        .mealStatus(request.mealStatus())
                        .painStatus(request.painStatus())
                        .moodStatus(request.moodStatus())
                        .cognitiveChanges(cognitiveChanges)
                        .build()));

        return DailyStatusResponse.from(status);
    }

    /**
     * 특정 날짜(기본 오늘)의 상태 조회. 환자/보호자/의사 공통.
     */
    public DailyStatusResponse getDailyStatus(Integer pCode, LocalDate date) {
        validateExists(pCode);
        LocalDate target = (date != null) ? date : LocalDate.now();
        PatientDailyStatus status = patientDailyStatusRepository
                .findByPCodeAndRecordDate(pCode, target)
                .orElseThrow(() -> new CustomException(ErrorCode.DAILY_STATUS_NOT_FOUND));
        return DailyStatusResponse.from(status);
    }

    /**
     * 상태 기록 전체 목록(최신순).
     */
    public List<DailyStatusResponse> getDailyStatusHistory(Integer pCode) {
        validateExists(pCode);
        return patientDailyStatusRepository.findAllByPCodeOrderByRecordDateDesc(pCode).stream()
                .map(DailyStatusResponse::from)
                .toList();
    }

    private String joinCognitiveChanges(List<String> changes) {
        if (changes == null || changes.isEmpty()) {
            return null;
        }
        return String.join(",", changes);
    }

    // ===== 다른 도메인에서 재사용하는 헬퍼 =====

    public Patient getPatient(Integer pCode) {
        return patientRepository.findById(pCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PATIENT_NOT_FOUND));
    }

    /**
     * 환자 이름 조회. 이름은 회원(Member) 에서 관리하므로 p_code -> 회원 으로 조회한다.
     */
    public String getPatientName(Integer pCode) {
        return getPatientMember(getPatient(pCode)).getName();
    }

    /**
     * 환자와 연결된 회원 조회. 이름/생년월일 등 공통 정보는 회원(Member) 에서 관리한다.
     */
    private Member getPatientMember(Patient patient) {
        return memberRepository.findByUserId(patient.getUserId())
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public void validateExists(Integer pCode) {
        if (!patientRepository.existsById(pCode)) {
            throw new CustomException(ErrorCode.PATIENT_NOT_FOUND);
        }
    }

    private void validateOwner(String email, Integer pCode) {
        Patient patient = getPatient(pCode);
        if (!patient.getUserId().equals(email)) { // 추후 의사,보호자 domain 업데이트 시 수정 예정
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}

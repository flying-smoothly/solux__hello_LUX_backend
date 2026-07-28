package com.eum.hello_lux_quiz.domain.patient.service;

import com.eum.hello_lux_quiz.domain.member.repository.MemberRepository;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientInfoResponse;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientRegisterRequest;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientRegisterResponse;
import com.eum.hello_lux_quiz.domain.patient.dto.PatientUpdateRequest;
import com.eum.hello_lux_quiz.domain.patient.entity.Patient;
import com.eum.hello_lux_quiz.domain.patient.repository.PatientRepository;
import com.eum.hello_lux_quiz.global.exception.CustomException;
import com.eum.hello_lux_quiz.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// quiz_llm 의 service.PatientService 와 빈 이름이 겹치지 않도록 명시적 이름 지정
@Service("authPatientService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final MemberRepository memberRepository;

    /**
     * 환자 기본 정보 등록. 환자 코드(p_code)를 발급하며 상세 정보를 함께 저장한다.
     * 이름/생년월일은 회원(Member) 에서 관리하므로 저장하지 않는다.
     */
    @Transactional
    public PatientRegisterResponse register(String email, PatientRegisterRequest request) {
        if (!memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }


        if (patientRepository.existsByUserEmail(email)) {
            throw new CustomException(ErrorCode.PATIENT_ALREADY_REGISTERED);
        }

        Patient patient = patientRepository.save(Patient.builder()
                .userEmail(email)
                .gender(request.gender())
                .diagnosis(request.diagnosis())
                .personality(request.personality())
                .speechStyle(request.speechStyle())
                .build());

        return new PatientRegisterResponse(patient.getPCode(), "환자 등록 완료");
    }

    /**
     * 환자 정보 조회. 환자/보호자/의사 공통.
     * 이름은 회원(Member) 정보에서 조회한다.
     */
    public PatientInfoResponse getInfo(Integer pCode) {
        return PatientInfoResponse.from(getPatient(pCode), getPatientName(pCode));
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

    // ===== 다른 도메인에서 재사용하는 헬퍼 =====

    public Patient getPatient(Integer pCode) {
        return patientRepository.findById(pCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PATIENT_NOT_FOUND));
    }

    /**
     * 환자 이름 조회. 이름은 회원(Member) 에서 관리하므로 p_code -> 회원 으로 조회한다.
     */
    public String getPatientName(Integer pCode) {
        Patient patient = getPatient(pCode);
        return memberRepository.findByEmail(patient.getUserEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND))
                .getName();
    }

    public void validateExists(Integer pCode) {
        if (!patientRepository.existsById(pCode)) {
            throw new CustomException(ErrorCode.PATIENT_NOT_FOUND);
        }
    }

    private void validateOwner(String email, Integer pCode) {
        Patient patient = getPatient(pCode);
        if (!patient.getUserEmail().equals(email)) { // 추후 의사,보호자 domain 업데이트 시 수정 예정
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}

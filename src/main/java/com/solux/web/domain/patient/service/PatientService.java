package com.solux.web.domain.patient.service;

import com.solux.web.domain.member.entity.Member;
import com.solux.web.domain.member.repository.MemberRepository;
import com.solux.web.domain.patient.dto.PatientInfoResponse;
import com.solux.web.domain.patient.dto.PatientRegisterRequest;
import com.solux.web.domain.patient.dto.PatientRegisterResponse;
import com.solux.web.domain.patient.dto.PatientUpdateRequest;
import com.solux.web.domain.patient.entity.Patient;
import com.solux.web.domain.patient.entity.PatientProfile;
import com.solux.web.domain.patient.repository.PatientProfileRepository;
import com.solux.web.domain.patient.repository.PatientRepository;
import com.solux.web.global.exception.CustomException;
import com.solux.web.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final MemberRepository memberRepository;

    /**
     * 환자 기본 정보 등록. 환자 코드(p_code)를 발급하고 프로필을 생성한다.
     * 이름/생년월일은 회원 정보에서 가져온다.
     */
    @Transactional
    public PatientRegisterResponse register(String email, PatientRegisterRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (patientRepository.existsByUserEmail(email)) {
            throw new CustomException(ErrorCode.PATIENT_ALREADY_REGISTERED);
        }

        Patient patient = patientRepository.save(Patient.builder()
                .userEmail(email)
                .build());

        PatientProfile profile = PatientProfile.builder()
                .pCode(patient.getPCode())
                .name(member.getName())
                .birthDate(member.getBirthDate())
                .gender(request.gender())
                .diagnosis(request.diagnosis())
                .personality(request.personality())
                .style(request.speechStyle())
                .build();
        patientProfileRepository.save(profile);

        return new PatientRegisterResponse(patient.getPCode(), "환자 등록 완료");
    }

    /**
     * 환자 정보 조회. 환자/보호자/의사 공통.
     */
    public PatientInfoResponse getInfo(Integer pCode) {
        return PatientInfoResponse.from(getProfile(pCode));
    }

    /**
     * 환자 정보 수정(성격/말투). 본인만 가능.
     */
    @Transactional
    public void update(String email, Integer pCode, PatientUpdateRequest request) {
        validateOwner(email, pCode);
        PatientProfile profile = getProfile(pCode);
        profile.update(request.personality(), request.speechStyle());
    }

    /**
     * 환자 코드 조회. 본인만 가능.
     */
    public Integer getCode(String email, Integer pCode) {
        validateOwner(email, pCode);
        return pCode;
    }

    // ===== 다른 도메인에서 재사용하는 헬퍼 =====

    public PatientProfile getProfile(Integer pCode) {
        return patientProfileRepository.findById(pCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PATIENT_NOT_FOUND));
    }

    public void validateExists(Integer pCode) {
        if (!patientRepository.existsById(pCode)) {
            throw new CustomException(ErrorCode.PATIENT_NOT_FOUND);
        }
    }

    private void validateOwner(String email, Integer pCode) {
        Patient patient = patientRepository.findById(pCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PATIENT_NOT_FOUND));
        if (!patient.getUserEmail().equals(email)) { // 추후 의사,보호자 domain 업데이트 시 수정 예정
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}

package com.eum.hello_lux_quiz.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eum.hello_lux_quiz.domain.PatientProfile;
import com.eum.hello_lux_quiz.domain.VoiceSetting;
import com.eum.hello_lux_quiz.domain.member.entity.Member;
import com.eum.hello_lux_quiz.domain.member.repository.MemberRepository;
import com.eum.hello_lux_quiz.domain.patient.entity.Patient;
import com.eum.hello_lux_quiz.domain.patient.repository.PatientRepository;
import com.eum.hello_lux_quiz.global.exception.CustomException;
import com.eum.hello_lux_quiz.global.exception.ErrorCode;
import com.eum.hello_lux_quiz.repository.PatientProfileRepository;

import lombok.RequiredArgsConstructor;

/**
 * patient_profile 행의 존재를 보장하는 헬퍼.
 *
 * <p>환자 등록(POST /api/patient/register)은 patient 테이블에만 저장하는데
 * 음성 설정·상태 변경·퀴즈 생성은 patient_profile 을 조회하기 때문에,
 * 프로필 행이 없으면 "해당 환자의 프로필을 찾을 수 없습니다" 로 무조건 실패했다.</p>
 *
 * <p>등록 시점에 {@link #createFrom(Patient)} 로 행을 만들고,
 * 이미 등록돼 프로필이 없는 환자는 {@link #getOrCreate(Integer)} 가 최초 접근 시
 * patient 값을 그대로 옮겨 채워 넣는다.</p>
 */
@Component
@RequiredArgsConstructor
public class PatientProfileProvisioner {

    /** 프로필 생성 시 기본 상태값. */
    private static final String DEFAULT_PATIENT_STATUS = "유지";

    private final PatientProfileRepository patientProfileRepository;
    private final PatientRepository patientRepository;
    private final MemberRepository memberRepository;

    /**
     * 프로필을 조회하고, 없으면 patient 값으로 만들어서 돌려준다.
     * 환자 자체가 없으면 404 로 응답한다.
     */
    @Transactional
    public PatientProfile getOrCreate(Integer pCode) {
        return patientProfileRepository.findByPCode(pCode)
                .orElseGet(() -> createFrom(patientRepository.findById(pCode)
                        .orElseThrow(() -> new CustomException(ErrorCode.PATIENT_NOT_FOUND))));
    }

    /**
     * patient 행을 바탕으로 프로필 행을 만든다. 환자 등록 직후 호출한다.
     * 이름은 회원(Member)에서 관리하므로 user_id 로 조회해 복사한다.
     */
    @Transactional
    public PatientProfile createFrom(Patient patient) {
        PatientProfile profile = new PatientProfile();
        profile.setPCode(patient.getPCode());
        profile.setName(findMemberName(patient.getUserId()));
        profile.setPersonality(patient.getPersonality());
        profile.setStyle(patient.getSpeechStyle());
        profile.setPatientStatus(patient.getPatientStatus() != null
                ? patient.getPatientStatus()
                : DEFAULT_PATIENT_STATUS);

        if (patient.getCognitiveSupportLevel() != null) {
            profile.setCognitiveLevel(patient.getCognitiveSupportLevel());
        }
        if (patient.getGuardianCompanion() != null) {
            profile.setIsGuardianPresent(patient.getGuardianCompanion());
        }

        VoiceSetting voiceSetting = new VoiceSetting();
        voiceSetting.setSpeechStyle(patient.getSpeechStyle());
        profile.setVoiceSetting(voiceSetting);

        return patientProfileRepository.save(profile);
    }

    private String findMemberName(String userId) {
        return memberRepository.findByUserId(userId)
                .map(Member::getName)
                .orElse(null);
    }
}
package com.eum.hello_lux_quiz.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eum.hello_lux_quiz.domain.PatientProfile;
import com.eum.hello_lux_quiz.domain.VoiceSetting;
import com.eum.hello_lux_quiz.dto.PatientStatusUpdateRequest;
import com.eum.hello_lux_quiz.dto.VoiceSettingRequestDto;
import com.eum.hello_lux_quiz.repository.PatientProfileRepository;

@Service
public class PatientService {

    private final PatientProfileRepository patientProfileRepository;

    public PatientService(PatientProfileRepository patientProfileRepository) {
        this.patientProfileRepository = patientProfileRepository;
    }

    /**
     * 연동용 코드(patient_code) 또는 정수 문자열을 내부 환자 코드(p_code, Integer)로 변환해주는 메서드
     */
    public Integer resolvePCode(String patientCode) {
        if (patientCode == null || patientCode.isBlank()) {
            throw new IllegalArgumentException("환자 코드가 유효하지 않습니다.");
        }

        try {
            // "1" 같은 정수 형태의 문자열일 경우 그대로 Integer로 변환
            return Integer.parseInt(patientCode);
        } catch (NumberFormatException e) {
            // 만약 "AB37X2" 같은 별도의 문자열 연동 코드를 사용하는 경우
            // DB의 환자 연동 테이블 조회 로직을 여기에 구현하시면 됩니다.
            throw new IllegalArgumentException("올바른 환자 코드(숫자) 형식이 아닙니다: " + patientCode);
        }
    }

    /**
     * 의사 환자 난이도/상태 변경 (DoctorController 전용 - String 수신)
     */
    @Transactional
    public void updatePatientStatus(Integer pCode, String newStatus) {
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("환자 상태 값이 올바르지 않습니다.");
        }

        PatientProfile profile = patientProfileRepository.findByPCode(pCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 환자의 프로필을 찾을 수 없습니다. pCode=" + pCode));

        profile.updatePatientStatus(newStatus);
    }

    /**
     * 의사가 환자의 인지 상태, 인지 지원 수준, 보호자 동행 여부를 변경하는 기능 (DTO 수신)
     */
    @Transactional
    public void updatePatientStatus(Integer pCode, PatientStatusUpdateRequest requestDto) {
        String newStatus = requestDto.getPatientStatus();

        if (!"유지".equals(newStatus) && !"주의".equals(newStatus) && !"위험".equals(newStatus)) {
            throw new IllegalArgumentException("올바르지 않은 환자 상태 값입니다. (유지/주의/위험 중 선택)");
        }

        PatientProfile profile = patientProfileRepository.findByPCode(pCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 환자의 프로필을 찾을 수 없습니다. pCode=" + pCode));

        profile.updatePatientStatus(newStatus);

        if (requestDto.getCognitiveLevel() != null) {
            profile.setCognitiveLevel(requestDto.getCognitiveLevel());
        }
        if (requestDto.getIsGuardianPresent() != null) {
            profile.setIsGuardianPresent(requestDto.getIsGuardianPresent());
        }
    }

    /**
     * 환자의 음성 설정 7종을 저장 및 업데이트하는 기능
     */
    @Transactional
    public VoiceSetting updateVoiceSetting(Integer pCode, VoiceSettingRequestDto requestDto) {
        PatientProfile profile = patientProfileRepository.findByPCode(pCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 환자의 프로필을 찾을 수 없습니다. pCode=" + pCode));

        VoiceSetting currentSetting = profile.getVoiceSetting();
        if (currentSetting == null) {
            currentSetting = new VoiceSetting();
            profile.setVoiceSetting(currentSetting);
        }

        currentSetting.updateSetting(
                requestDto.getTtsSpeed(),
                requestDto.getSentenceLength(),
                requestDto.getIsHonorific(),
                requestDto.getIsAutoPlay(),
                requestDto.getIsRepeatGuide(),
                requestDto.getIsLowPressure(),
                requestDto.getIsPositiveFeedback(),
                requestDto.getSpeechStyle()
        );

        return profile.getVoiceSetting();
    }
}

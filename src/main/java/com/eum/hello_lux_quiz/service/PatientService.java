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

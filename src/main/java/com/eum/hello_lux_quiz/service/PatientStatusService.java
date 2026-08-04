package com.eum.hello_lux_quiz.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eum.hello_lux_quiz.domain.PatientProfile;
import com.eum.hello_lux_quiz.repository.PatientProfileRepository;

@Service
public class PatientStatusService {

    private final PatientProfileRepository patientProfileRepository;

    // 수동 생성자 주입
    public PatientStatusService(PatientProfileRepository patientProfileRepository) {
        this.patientProfileRepository = patientProfileRepository;
    }

    /**
     * 의사가 환자의 인지 상태(patient_status)를 변경하는 기능
     */
    @Transactional
    public void updatePatientStatus(Integer pCode, String newStatus) {
        // 입력값 검증 (유지, 주의, 위험 외 예외 처리)
        if (!"유지".equals(newStatus) && !"주의".equals(newStatus) && !"위험".equals(newStatus)) {
            throw new IllegalArgumentException("올바르지 않은 환자 상태 값입니다. (유지/주의/위험 중 선택)");
        }

        PatientProfile profile = patientProfileRepository.findByPCode(pCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 환자의 프로필을 찾을 수 없습니다. pCode=" + pCode));

        // 프로필 엔티티의 상태 변경 (JPA 더티 체킹에 의해 자동 DB 반영)
        profile.updatePatientStatus(newStatus);
    }
}

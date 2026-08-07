-- V2: 공유 DB 조회 인덱스

-- PK / UNIQUE 로 이미 커버되는 조회(patient_daily_status, doctor_report,
-- patient_quiz_level 등)는 중복이므로 넣지 않았다.

-- PatientRepository.findByUserId / existsByUserId / findAllByUserId
CREATE INDEX idx_patient__user_id ON patient (user_id);

-- GuardianRepository.findAllByUserId
-- (uk_guardian__p_code_user_id 는 선두 컬럼이 p_code 라 user_id 단독 조회에 못 쓴다)
CREATE INDEX idx_guardian__user_id ON guardian (user_id);

-- DoctorPatientRepository.findAllByUserId (위와 같은 이유)
CREATE INDEX idx_doctor_patient__user_id ON doctor_patient (user_id);

-- StatusMemoRepository.findAllByPCodeOrderByRecordDateDesc
CREATE INDEX idx_status_memo__p_code_record_date ON status_memo (p_code, record_date);

-- LifeDbRepository.findByPCode
CREATE INDEX idx_삶의db__p_code ON 삶의db (p_code);

-- DetailRepository.findByMemoryId
CREATE INDEX idx_세분화__memory_id ON 세분화 (memory_id);

-- QuizSetRepository.countByPCode
CREATE INDEX idx_퀴즈세트__p_code ON 퀴즈세트 (p_code);

-- QuizItemRepository.findBySetId / findBySetIdAndQuizNum
-- (PK 가 (p_code, quiz_num, set_id) 라 set_id 로 시작하는 조회에 PK 를 못 쓴다)
CREATE INDEX idx_퀴즈응시__set_id_quiz_num ON 퀴즈응시 (set_id, quiz_num);

-- QuizResultRepository.findByPCode / findByPCodeAndDate / findByPCodeAndDateBetween
CREATE INDEX idx_퀴즈_결과__p_code_date ON 퀴즈_결과 (p_code, date);

-- HintRepository.findByPCodeAndEventId
CREATE INDEX idx_힌트__p_code_event_id ON 힌트 (p_code, event_id);
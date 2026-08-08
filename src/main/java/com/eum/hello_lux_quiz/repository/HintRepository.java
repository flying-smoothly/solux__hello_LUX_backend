package com.eum.hello_lux_quiz.repository;

import com.eum.hello_lux_quiz.domain.Hint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HintRepository extends JpaRepository<Hint, Long> {

    // 환자 코드(pCode)와 사건 ID(eventId)로 힌트 단건 조회 (Level 2, 3 오답 시 활용)
    Optional<Hint> findByPCodeAndEventId(Long pCode, Long eventId);
}

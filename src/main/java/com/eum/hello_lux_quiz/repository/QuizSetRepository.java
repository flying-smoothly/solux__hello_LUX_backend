package com.eum.hello_lux_quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eum.hello_lux_quiz.domain.QuizSet;

@Repository
public interface QuizSetRepository extends JpaRepository<QuizSet, Integer> {

    /** 특정 환자(pCode)의 기존 퀴즈 세트 개수. 다음 세트 순번 계산에 사용. */
    long countByPCode(Integer pCode);
}

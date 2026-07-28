package com.eum.hello_lux_quiz.domain.doctor.repository;

import com.eum.hello_lux_quiz.domain.doctor.entity.PatientQuizLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PatientQuizLevelRepository extends JpaRepository<PatientQuizLevel, Long> {

    // 필드명이 pCode 라 파생 쿼리 파싱 이슈가 있어 JPQL 로 명시한다.
    @Query("select l from PatientQuizLevel l where l.pCode = :pCode and l.quizType = :quizType")
    Optional<PatientQuizLevel> findByPCodeAndQuizType(@Param("pCode") Integer pCode, @Param("quizType") String quizType);
}

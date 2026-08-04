package com.eum.hello_lux_quiz.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eum.hello_lux_quiz.domain.QuizResult;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Integer> {

    // 환자 번호(pCode) 기준 전체 조회
    List<QuizResult> findByPCode(Integer pCode);

    // 특정 날짜 조회 (Entity의 필드명 resultDate에 맞춤)
    Optional<QuizResult> findByPCodeAndResultDate(Integer pCode, LocalDate resultDate);

    // 특정 기간(시작일~종료일) 조회 (Entity의 필드명 resultDate에 맞춤)
    List<QuizResult> findByPCodeAndResultDateBetween(Integer pCode, LocalDate from, LocalDate to);
}

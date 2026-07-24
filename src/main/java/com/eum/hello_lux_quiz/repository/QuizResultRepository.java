package com.eum.hello_lux_quiz.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eum.hello_lux_quiz.domain.QuizResult;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Integer> { // 💡 Long -> Integer 변경

    List<QuizResult> findByPCode(Integer pCode); // 💡 Long -> Integer 변경

    Optional<QuizResult> findByPCodeAndDate(Integer pCode, LocalDate date); // 💡 Long -> Integer 변경
}

package com.eum.hello_lux_quiz.repository;

import com.eum.hello_lux_quiz.domain.QuizItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizItemRepository extends JpaRepository<QuizItem, Integer> { // 💡 JpaRepository<..., Long> -> Integer로 변경

    // 특정 퀴즈 세트(setId)에 속한 문항 리스트 조회
    List<QuizItem> findBySetId(Integer setId); // 💡 Long -> Integer로 변경

    Optional<QuizItem> findBySetIdAndQuizNum(Integer setId, Integer quizNum); // 💡 Long -> Integer로 변경
}

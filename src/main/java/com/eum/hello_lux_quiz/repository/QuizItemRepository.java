package com.eum.hello_lux_quiz.repository;

import com.eum.hello_lux_quiz.domain.QuizItem;
import com.eum.hello_lux_quiz.domain.QuizItemId; //  복합키 클래스 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizItemRepository extends JpaRepository<QuizItem, QuizItemId> { // Integer -> QuizItemId로 변경

    // 특정 퀴즈 세트(setId)에 속한 문항 리스트 조회
    List<QuizItem> findBySetId(Integer setId);

    Optional<QuizItem> findBySetIdAndQuizNum(Integer setId, Integer quizNum);
}

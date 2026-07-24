package com.eum.hello_lux_quiz.repository;

import com.eum.hello_lux_quiz.domain.QuizFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizFeedbackRepository extends JpaRepository<QuizFeedback, Integer> { // 💡 Long -> Integer 변경
}

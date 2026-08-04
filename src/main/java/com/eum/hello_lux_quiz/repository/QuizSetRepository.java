package com.eum.hello_lux_quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eum.hello_lux_quiz.domain.QuizSet;

@Repository
public interface QuizSetRepository extends JpaRepository<QuizSet, Integer> {

    int countByPCode(Integer pCode);
}

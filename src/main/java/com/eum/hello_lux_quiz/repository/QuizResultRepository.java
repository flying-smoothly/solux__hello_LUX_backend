package com.eum.hello_lux_quiz.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eum.hello_lux_quiz.domain.QuizResult;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Integer> {

    // 필드는 pCode 인데 getter 가 getPCode() 라서, 메서드 이름만으로는 Spring Data 가
    // 속성명을 'PCode' 로 해석해 UnknownPathException 이 난다.
    // LifeDbRepository 와 동일하게 @Query 로 소문자 pCode 를 명시한다.
    @Query("SELECT q FROM QuizResult q WHERE q.pCode = :pCode")
    List<QuizResult> findByPCode(@Param("pCode") Integer pCode);

    @Query("SELECT q FROM QuizResult q WHERE q.pCode = :pCode AND q.date = :date")
    Optional<QuizResult> findByPCodeAndDate(@Param("pCode") Integer pCode, @Param("date") LocalDate date);

    @Query("SELECT q FROM QuizResult q WHERE q.pCode = :pCode AND q.date BETWEEN :from AND :to")
    List<QuizResult> findByPCodeAndDateBetween(@Param("pCode") Integer pCode,
                                               @Param("from") LocalDate from,
                                               @Param("to") LocalDate to);
}

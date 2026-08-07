package com.eum.hello_lux_quiz.repository;

import com.eum.hello_lux_quiz.domain.QuizItem;
import com.eum.hello_lux_quiz.domain.QuizItemId; //  복합키 클래스 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizItemRepository extends JpaRepository<QuizItem, QuizItemId> { // Integer -> QuizItemId로 변경

    // 특정 퀴즈 세트(setId)에 속한 문항 리스트 조회
    List<QuizItem> findBySetId(Integer setId);

    Optional<QuizItem> findBySetIdAndQuizNum(Integer setId, Integer quizNum);
    // 복합키(pCode, setId, quizNum) 전체로 조회한다. setId+quizNum 만으로도 행은 하나지만,
    // pCode 를 조건에 넣어야 남의 환자 문항을 집어오는 일이 생기지 않는다.
    // QuizResultRepository 와 같은 이유로 @Query 를 쓴다(getter 가 getpCode()).
    @Query("SELECT q FROM QuizItem q WHERE q.pCode = :pCode AND q.setId = :setId AND q.quizNum = :quizNum")
    Optional<QuizItem> findByPCodeAndSetIdAndQuizNum(@Param("pCode") Integer pCode,
                                                     @Param("setId") Integer setId,
                                                     @Param("quizNum") Integer quizNum);
}

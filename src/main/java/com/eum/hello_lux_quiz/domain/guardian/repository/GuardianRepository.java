package com.eum.hello_lux_quiz.domain.guardian.repository;

import com.eum.hello_lux_quiz.domain.guardian.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {

    List<Guardian> findAllByUserId(String userId);

    // 필드명이 pCode(두 번째 글자 대문자)라 파생 쿼리 파싱이 'PCode'로 오인하므로 JPQL 로 명시한다.
    @Query("select count(g) > 0 from Guardian g where g.pCode = :pCode and g.userId = :userId")
    boolean existsByPCodeAndUserId(@Param("pCode") Integer pCode, @Param("userId") String userId);
}

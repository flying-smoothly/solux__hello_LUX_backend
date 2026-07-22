package com.solux.web.domain.guardian.repository;

import com.solux.web.domain.guardian.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {

    List<Guardian> findAllByUserEmail(String userEmail);

    // 필드명이 pCode(두 번째 글자 대문자)라 파생 쿼리 파싱이 'PCode'로 오인하므로 JPQL 로 명시한다.
    @Query("select count(g) > 0 from Guardian g where g.pCode = :pCode and g.userEmail = :userEmail")
    boolean existsByPCodeAndUserEmail(@Param("pCode") Integer pCode, @Param("userEmail") String userEmail);
}

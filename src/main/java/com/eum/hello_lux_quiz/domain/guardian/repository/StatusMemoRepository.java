package com.eum.hello_lux_quiz.domain.guardian.repository;

import com.eum.hello_lux_quiz.domain.guardian.entity.StatusMemo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatusMemoRepository extends JpaRepository<StatusMemo, Long> {

    // 필드명이 pCode 라 파생 쿼리 파싱 이슈가 있어 JPQL 로 명시한다.
    @Query("select m from StatusMemo m where m.pCode = :pCode order by m.createdAt desc")
    List<StatusMemo> findAllByPCodeOrderByCreatedAtDesc(@Param("pCode") Integer pCode);
}

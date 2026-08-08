package com.eum.hello_lux_quiz.repository;

import com.eum.hello_lux_quiz.domain.LifeDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LifeDbRepository extends JpaRepository<LifeDb, Integer> {

    // @Query를 통해 소문자 pCode 필드를 명확하게 지정합니다.
    @Query("SELECT l FROM LifeDb l WHERE l.pCode = :pCode")
    List<LifeDb> findByPCode(@Param("pCode") Integer pCode);
}

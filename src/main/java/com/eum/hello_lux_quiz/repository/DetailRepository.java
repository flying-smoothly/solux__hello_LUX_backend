package com.eum.hello_lux_quiz.repository;

import com.eum.hello_lux_quiz.domain.DetailEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailRepository extends JpaRepository<DetailEvent, Integer> {

    // 특정 메모리 ID에 해당하는 세분화 사건 목록 조회
    List<DetailEvent> findByMemoryId(Integer memoryId);
}

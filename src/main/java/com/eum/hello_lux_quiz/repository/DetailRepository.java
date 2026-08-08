package com.eum.hello_lux_quiz.repository;

import com.eum.hello_lux_quiz.domain.DetailEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DetailRepository extends JpaRepository<DetailEvent, Integer> {

    // 특정 메모리 ID에 해당하는 세분화 사건 목록 조회
    List<DetailEvent> findByMemoryId(Integer memoryId);

    @Modifying
    @Transactional
    @Query("UPDATE DetailEvent d SET d.photoUrl = :photoUrl WHERE d.eventId = :eventId")
    int updatePhotoUrlByEventId(@Param("eventId") Integer eventId, @Param("photoUrl") String photoUrl);
}

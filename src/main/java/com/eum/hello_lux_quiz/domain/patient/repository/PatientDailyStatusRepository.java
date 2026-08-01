package com.eum.hello_lux_quiz.domain.patient.repository;

import com.eum.hello_lux_quiz.domain.patient.entity.PatientDailyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientDailyStatusRepository extends JpaRepository<PatientDailyStatus, Long> {

    // 필드명이 pCode 라 파생 쿼리 파싱 이슈가 있어 JPQL 로 명시한다.
    @Query("select s from PatientDailyStatus s where s.pCode = :pCode and s.recordDate = :recordDate")
    Optional<PatientDailyStatus> findByPCodeAndRecordDate(@Param("pCode") Integer pCode,
                                                          @Param("recordDate") LocalDate recordDate);

    @Query("select s from PatientDailyStatus s where s.pCode = :pCode order by s.recordDate desc")
    List<PatientDailyStatus> findAllByPCodeOrderByRecordDateDesc(@Param("pCode") Integer pCode);
}
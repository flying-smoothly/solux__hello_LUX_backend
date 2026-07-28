package com.eum.hello_lux_quiz.domain.doctor.repository;

import com.eum.hello_lux_quiz.domain.doctor.entity.DoctorReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DoctorReportRepository extends JpaRepository<DoctorReport, Long> {

    // 필드명이 pCode 라 파생 쿼리 파싱 이슈가 있어 JPQL 로 명시한다.
    @Query("select r from DoctorReport r where r.pCode = :pCode and r.userEmail = :userEmail")
    Optional<DoctorReport> findByPCodeAndUserEmail(@Param("pCode") Integer pCode, @Param("userEmail") String userEmail);
}

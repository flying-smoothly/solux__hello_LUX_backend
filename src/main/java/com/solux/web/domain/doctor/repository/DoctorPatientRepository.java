package com.solux.web.domain.doctor.repository;

import com.solux.web.domain.doctor.entity.DoctorPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorPatientRepository extends JpaRepository<DoctorPatient, Long> {

    List<DoctorPatient> findAllByUserEmail(String userEmail);

    // 필드명이 pCode 라 파생 쿼리 파싱 이슈가 있어 JPQL 로 명시한다.
    @Query("select count(dp) > 0 from DoctorPatient dp where dp.pCode = :pCode and dp.userEmail = :userEmail")
    boolean existsByPCodeAndUserEmail(@Param("pCode") Integer pCode, @Param("userEmail") String userEmail);
}

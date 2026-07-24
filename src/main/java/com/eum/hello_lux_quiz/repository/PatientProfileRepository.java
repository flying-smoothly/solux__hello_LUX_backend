package com.eum.hello_lux_quiz.repository;

import com.eum.hello_lux_quiz.domain.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Integer> {

    @Query("SELECT p FROM PatientProfile p WHERE p.pCode = :pCode")
    Optional<PatientProfile> findByPCode(@Param("pCode") Integer pCode);
}

package com.eum.hello_lux_quiz.domain.patient.repository;

import com.eum.hello_lux_quiz.domain.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

    Optional<Patient> findByUserEmail(String userEmail);

    boolean existsByUserEmail(String userEmail);

    List<Patient> findAllByUserEmail(String userEmail);
}

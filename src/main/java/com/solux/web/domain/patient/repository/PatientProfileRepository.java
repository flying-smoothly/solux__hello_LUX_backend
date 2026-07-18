package com.solux.web.domain.patient.repository;

import com.solux.web.domain.patient.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Integer> {
}

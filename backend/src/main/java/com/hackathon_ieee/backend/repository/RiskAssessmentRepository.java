package com.hackathon_ieee.backend.repository;

import com.hackathon_ieee.backend.model.RiskAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessmentEntity, String> {
}
package com.hackathon_ieee.backend.repository;

import com.hackathon_ieee.backend.model.RiskAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessmentEntity, String> {

	List<RiskAssessmentEntity> findByLocationNameOrderByAssessedAtDesc(String locationName);

	List<RiskAssessmentEntity> findAllByOrderByAssessedAtDesc();
}
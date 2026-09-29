package com.hackathon_ieee.backend.repository;

import com.hackathon_ieee.backend.model.CitizenReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitizenReportRepository extends JpaRepository<CitizenReportEntity, String> {
	List<CitizenReportEntity> findAllByOrderByTimestampDesc();
}

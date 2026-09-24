package com.hackathon_ieee.backend.repository;

import com.hackathon_ieee.backend.model.CitizenReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitizenReportRepository extends JpaRepository<CitizenReportEntity, String> {
}

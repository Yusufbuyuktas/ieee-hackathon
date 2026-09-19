package com.hackathon_ieee.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RiskAssessmentResponse {
    private String id;
    private String fhirRiskAssessmentId;
}
package com.hackathon_ieee.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class RiskAssessmentListItemDto {
    private String id;
    private String locationName;
    private Integer stationNo;
    private OffsetDateTime timestamp;
    private RiskValuesDto carcinogenicRisk;
    private RiskValuesDto totalHazardIndex;
    private String riskLevel;
    private String basisNote;
    private String sourceType;
    private String citation;
    private String fhirRiskAssessmentId;

    @Getter
    @AllArgsConstructor
    public static class RiskValuesDto {
        private Double child;
        private Double adult;
    }
}
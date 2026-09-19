package com.hackathon_ieee.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class RiskStatusResponse {
    private String location;
    private String currentRiskLevel;
    private String parameter;
    private Double value;
    private String unit;
    private Double threshold;
    private String standard;
    private String reason;
    private OffsetDateTime lastUpdated;
}
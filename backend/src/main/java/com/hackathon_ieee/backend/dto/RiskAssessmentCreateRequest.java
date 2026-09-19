package com.hackathon_ieee.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RiskAssessmentCreateRequest {
    private String locationName;
    private Integer stationNo;
    private OffsetDateTime timestamp;
    private RiskValuesDto carcinogenicRisk;
    private RiskValuesDto totalHazardIndex;
    private Boolean sourceConcludedHighRisk;
    private String basisNote;
    private String sourceType;
    private String citation;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RiskValuesDto {
        private Double child;
        private Double adult;
    }
}
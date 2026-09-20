package com.hackathon_ieee.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class ObservationListItemDto {
    private String id;
    private String locationName;
    private CoordinatesDto coordinates;
    private OffsetDateTime timestamp;
    private String parameter;
    private Double value;
    private String unit;
    private boolean belowDetectionLimit;
    private String sampleType;
    private String sourceType;
    private boolean riskFlagged;
}
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
public class ObservationCreateRequest {
    private String locationName;
    private Integer stationNo;
    private CoordinatesDto coordinates;
    private OffsetDateTime timestamp;
    private String parameter;
    private Double value;
    private String unit;
    private boolean belowDetectionLimit;
    private String sampleType;
    private String sourceType;
    private String citation;
    private String method;
    private String coordinateSource;
    private WaterQualityDto waterQuality;
}
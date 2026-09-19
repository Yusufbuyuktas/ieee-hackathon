package com.hackathon_ieee.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LocationListItemDto {
    private String locationName;
    private Integer stationNo;
    private List<String> sampleTypes;
    private CoordinatesDto coordinates;
}
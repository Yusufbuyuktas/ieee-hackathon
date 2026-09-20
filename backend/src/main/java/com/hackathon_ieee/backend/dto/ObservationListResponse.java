package com.hackathon_ieee.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ObservationListResponse {
    private List<ObservationListItemDto> results;
}
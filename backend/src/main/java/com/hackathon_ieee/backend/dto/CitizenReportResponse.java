package com.hackathon_ieee.backend.dto;

import com.hackathon_ieee.backend.enums.AiValidationStatus;

public record CitizenReportResponse(
        String id,
        AiValidationStatus aiValidationStatus,
        Double aiConfidence
) {
}

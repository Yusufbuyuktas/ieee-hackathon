package com.hackathon_ieee.backend.dto;

import com.hackathon_ieee.backend.enums.AiValidationStatus;
import com.hackathon_ieee.backend.enums.CitizenReportCategory;

public record CitizenReportListItemDto(
        String id,
        String photoUrl,
        CitizenReportCategory category,
        String note,
        Double latitude,
        Double longitude,
        String timestamp,
        AiValidationStatus aiValidationStatus,
        Double aiConfidence,
        String aiExplanation,
        String fhirObservationId
) {
}

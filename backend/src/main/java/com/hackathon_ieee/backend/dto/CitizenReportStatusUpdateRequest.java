package com.hackathon_ieee.backend.dto;

import com.hackathon_ieee.backend.enums.AiValidationStatus;

public record CitizenReportStatusUpdateRequest(
        AiValidationStatus status
) {
}
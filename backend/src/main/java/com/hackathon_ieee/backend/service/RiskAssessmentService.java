package com.hackathon_ieee.backend.service;

import com.hackathon_ieee.backend.dto.RiskAssessmentCreateRequest;
import com.hackathon_ieee.backend.dto.RiskAssessmentResponse;
import com.hackathon_ieee.backend.enums.RiskLevel;
import com.hackathon_ieee.backend.enums.SourceType;
import com.hackathon_ieee.backend.model.RiskAssessmentEntity;
import com.hackathon_ieee.backend.repository.RiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RiskAssessmentService {
    private final RiskAssessmentRepository repository;
    private final FhirClientService fhirClient;

    public RiskAssessmentResponse create(RiskAssessmentCreateRequest request) {
        return create(request, "risk-" + UUID.randomUUID());
    }

    public RiskAssessmentResponse create(RiskAssessmentCreateRequest request, String id) {
        if (request.getTimestamp() == null || request.getLocationName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "location_name and timestamp are required");
        }

        SourceType sourceType;

        try {
            sourceType = SourceType.valueOf(request.getSourceType().toUpperCase());
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid source_type");
        }

        RiskAssessmentEntity entity = new RiskAssessmentEntity(id, request.getLocationName(),request.getTimestamp(), sourceType);

        entity.setStationNo(request.getStationNo());
        entity.setBasisNote(request.getBasisNote());
        entity.setCitation(request.getCitation());

        if (request.getCarcinogenicRisk() != null) {
            entity.setCarcinogenicRiskChild(request.getCarcinogenicRisk().getChild());
            entity.setCarcinogenicRiskAdult(request.getCarcinogenicRisk().getAdult());
        }

        if (request.getTotalHazardIndex() != null) {
            entity.setTotalHazardIndexChild(request.getTotalHazardIndex().getChild());
            entity.setTotalHazardIndexAdult(request.getTotalHazardIndex().getAdult());
        }

        entity.setRiskLevel(isHigh(entity) ? RiskLevel.HIGH : RiskLevel.NORMAL);
        entity = repository.save(entity);
        entity.setFhirRiskAssessmentId(fhirClient.createRiskAssessment(entity));
        entity = repository.save(entity);

        return new RiskAssessmentResponse(entity.getId(), entity.getFhirRiskAssessmentId());
    }

    private boolean isHigh(RiskAssessmentEntity entity) {
        return (entity.getCarcinogenicRiskChild() != null && entity.getCarcinogenicRiskChild() > 1.0)
                || (entity.getCarcinogenicRiskAdult() != null && entity.getCarcinogenicRiskAdult() > 1.0);
    }
}
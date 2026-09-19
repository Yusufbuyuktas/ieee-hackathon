package com.hackathon_ieee.backend.service;

import com.hackathon_ieee.backend.dto.RiskAssessmentCreateRequest;
import com.hackathon_ieee.backend.dto.RiskAssessmentListItemDto;
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
import java.util.List;

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
        if (request.getSourceConcludedHighRisk() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "source_concluded_high_risk is required");
        }

        SourceType sourceType;

        try {
            sourceType = SourceType.valueOf(request.getSourceType().toUpperCase());
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid source_type");
        }

        RiskAssessmentEntity entity = new RiskAssessmentEntity(id, request.getLocationName(),request.getTimestamp(), sourceType);

        entity.setStationNo(request.getStationNo());
        entity.setSourceConcludedHighRisk(request.getSourceConcludedHighRisk());
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

        public List<RiskAssessmentListItemDto> findAll(String location) {
            List<RiskAssessmentEntity> entities = location == null || location.isBlank()
                ? repository.findAllByOrderByAssessedAtDesc()
                : repository.findByLocationNameOrderByAssessedAtDesc(location);

            return entities.stream().map(this::toListItem).toList();
        }

        private RiskAssessmentListItemDto toListItem(RiskAssessmentEntity entity) {
            RiskAssessmentListItemDto.RiskValuesDto carcinogenicRisk = new RiskAssessmentListItemDto.RiskValuesDto(
                    entity.getCarcinogenicRiskChild(), entity.getCarcinogenicRiskAdult());

            RiskAssessmentListItemDto.RiskValuesDto totalHazardIndex = new RiskAssessmentListItemDto.RiskValuesDto(
                    entity.getTotalHazardIndexChild(), entity.getTotalHazardIndexAdult());

            return new RiskAssessmentListItemDto(entity.getId(), entity.getLocationName(), entity.getStationNo(),
                entity.getAssessedAt(), carcinogenicRisk, totalHazardIndex,
                entity.getRiskLevel() == null ? null : entity.getRiskLevel().name().toLowerCase(),
                Boolean.TRUE.equals(entity.getSourceConcludedHighRisk()), entity.getBasisNote(),
                entity.getSourceType().name().toLowerCase(), entity.getCitation(),
                entity.getFhirRiskAssessmentId());
        }

    private boolean isHigh(RiskAssessmentEntity entity) {
        return Boolean.TRUE.equals(entity.getSourceConcludedHighRisk())
            || (entity.getTotalHazardIndexChild() != null && entity.getTotalHazardIndexChild() > 1.0)
            || (entity.getTotalHazardIndexAdult() != null && entity.getTotalHazardIndexAdult() > 1.0);
    }
}
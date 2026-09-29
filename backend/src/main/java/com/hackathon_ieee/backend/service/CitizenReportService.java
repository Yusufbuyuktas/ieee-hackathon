package com.hackathon_ieee.backend.service;

import com.hackathon_ieee.backend.dto.CitizenReportResponse;
import com.hackathon_ieee.backend.dto.CitizenReportListItemDto;
import com.hackathon_ieee.backend.enums.AiValidationStatus;
import com.hackathon_ieee.backend.enums.CitizenReportCategory;
import com.hackathon_ieee.backend.model.CitizenReportEntity;
import com.hackathon_ieee.backend.repository.CitizenReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CitizenReportService {
    private static final double APPROVAL_THRESHOLD = 0.80;

    private final CitizenReportRepository repository;
    private final FileStorageService fileStorageService;
    private final AiModerationClient aiModerationClient;
    private final FhirClientService fhirClientService;

    public CitizenReportResponse create(MultipartFile photo, String categoryValue, String note,
                                        Double latitude, Double longitude, String timestamp) {
        CitizenReportCategory category = parseCategory(categoryValue);
        if (timestamp == null || timestamp.isBlank()) {
            throw new IllegalArgumentException("timestamp is required");
        }

        String filename = fileStorageService.store(photo);
        CitizenReportEntity entity = new CitizenReportEntity(
                "cit-" + UUID.randomUUID(), "/uploads/" + filename, category,
                note, latitude, longitude, timestamp
        );

        AiModerationClient.Result aiResult = aiModerationClient.moderate(filename, aiCategory(category));
        if (aiResult == null) {
            entity.setAiValidationStatus(AiValidationStatus.AI_SERVISI_ERISILEMEDI);
        } else {
            entity.setAiConfidence(aiResult.guvenSkoru());
            entity.setAiExplanation(aiResult.aciklama());
            entity.setAiValidationStatus(aiResult.tutarli() && aiResult.guvenSkoru() >= APPROVAL_THRESHOLD
                    ? AiValidationStatus.ONAYLANDI : AiValidationStatus.INCELEMEDE);
        }

        entity = repository.save(entity);
        entity.setFhirObservationId(fhirClientService.createCitizenReportObservation(entity));
        entity = repository.save(entity);
        return new CitizenReportResponse(entity.getId(), entity.getAiValidationStatus(), entity.getAiConfidence());
    }

    public List<CitizenReportListItemDto> findAll() {
        return repository.findAllByOrderByTimestampDesc().stream()
                .map(entity -> new CitizenReportListItemDto(
                        entity.getId(), entity.getPhotoUrl(), entity.getCategory(), entity.getNote(),
                        entity.getLatitude(), entity.getLongitude(), entity.getTimestamp(),
                        entity.getAiValidationStatus(), entity.getAiConfidence(), entity.getAiExplanation(),
                        entity.getFhirObservationId()))
                .toList();
    }

    private CitizenReportCategory parseCategory(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("category is required");
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.equals("BULANIK_SU")) {
            normalized = "BULANIK";
        }
        try {
            return CitizenReportCategory.valueOf(normalized);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("invalid category: " + value, exception);
        }
    }

    private String aiCategory(CitizenReportCategory category) {
        return category == CitizenReportCategory.BULANIK ? "bulanik_su" : category.name().toLowerCase(Locale.ROOT);
    }
}

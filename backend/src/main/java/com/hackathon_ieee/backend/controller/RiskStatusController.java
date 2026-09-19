package com.hackathon_ieee.backend.controller;

import com.hackathon_ieee.backend.config.ThresholdConfig;
import com.hackathon_ieee.backend.dto.RiskStatusResponse;
import com.hackathon_ieee.backend.model.ObservationEntity;
import com.hackathon_ieee.backend.repository.ObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;

@RestController
@RequestMapping("/api/risk-status")
@RequiredArgsConstructor
public class RiskStatusController {
    private final ObservationRepository observationRepository;
    private final ThresholdConfig thresholdConfig;

    @GetMapping
    public RiskStatusResponse get(@RequestParam String location) {
        ObservationEntity entity = observationRepository.findAll().stream()
                .filter(observation -> matchesLocation(location, observation.getLocationName()))
                .filter(ObservationEntity::isRiskFlagged)
                .max(Comparator.comparing(ObservationEntity::getObservedAt))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "risk status not found"));

        var thresholds = thresholdConfig.getThresholds(entity.getParameter());
        var standard = thresholds.entrySet().stream()
                .filter(entry -> entity.getValue() > entry.getValue())
                .findFirst()
                .orElseThrow();
        return new RiskStatusResponse(location, "high", entity.getParameter().name().toLowerCase(), entity.getValue(),
                entity.getUnit(), standard.getValue(), standard.getKey().name(),
                "Olculen " + entity.getParameter().name().toLowerCase() + " degeri esik degerini asiyor.", entity.getObservedAt());
    }

        private boolean matchesLocation(String requested, String actual) {
                if (requested.equals(actual)) return true;
                String normalized = actual.toLowerCase()
                        .replace("ergene havzasi - ", "ergene-")
                        .replace(" ", "-");
                return requested.toLowerCase().replace("-0", "-").equals(normalized);
        }
}
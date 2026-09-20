package com.hackathon_ieee.backend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hackathon_ieee.backend.dto.ObservationCreateRequest;
import com.hackathon_ieee.backend.dto.RiskAssessmentCreateRequest;
import com.hackathon_ieee.backend.repository.ObservationRepository;
import com.hackathon_ieee.backend.repository.RiskAssessmentRepository;
import com.hackathon_ieee.backend.service.ObservationService;
import com.hackathon_ieee.backend.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final ObjectMapper objectMapper;
    private final ObservationRepository observationRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final ObservationService observationService;
    private final RiskAssessmentService riskAssessmentService;

    @Override
    public void run(String... args) throws Exception {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.registerModule(new JavaTimeModule());
        seedObservations("ergene-2013-measurements.json");
        seedObservations("ergene-2025-measurements.json");
        seedObservations("ergene-2021-measurements.json");
        seedRisks("ergene-2021-risk.json");
        seedRisks("ergene-2025-risk.json");
    }

    private void seedObservations(String fileName) throws IOException {
        JsonNode records = read(fileName);
        if (records == null || !records.isArray()) return;
        for (JsonNode record : records) {
            String id = record.path("measurement_id").asText(null);
            if (id != null && !observationRepository.existsById(id)) {
                ObservationCreateRequest request = objectMapper.treeToValue(record, ObservationCreateRequest.class);
                observationService.create(request, id);
            }
        }
    }

    private void seedRisks(String fileName) throws IOException {
        JsonNode records = read(fileName);

        if (records == null || !records.isArray()) return;
        for (JsonNode record : records) {
            String id = record.path("assessment_id").asText(null);
            if (id != null && !riskAssessmentRepository.existsById(id)) {
                RiskAssessmentCreateRequest request = objectMapper.treeToValue(record, RiskAssessmentCreateRequest.class);
                riskAssessmentService.create(request, id);
            }
        }
    }

    private JsonNode read(String fileName) throws IOException {
        // Path external = Path.of("..", "data", fileName);
        Path external = Path.of("data", fileName);
        if (Files.exists(external)) {
            return objectMapper.readTree(Files.newInputStream(external));
        }
        ClassPathResource resource = new ClassPathResource("data/" + fileName);
        if (resource.exists()) {
            try (InputStream input = resource.getInputStream()) {
                return objectMapper.readTree(input);
            }
        }
        return null;
    }
}
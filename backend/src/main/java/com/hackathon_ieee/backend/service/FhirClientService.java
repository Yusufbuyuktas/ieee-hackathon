package com.hackathon_ieee.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon_ieee.backend.model.ObservationEntity;
import com.hackathon_ieee.backend.model.RiskAssessmentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FhirClientService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${fhir.server-url}")
    private String serverUrl;

    public String createObservation(ObservationEntity entity) {
        Map<String, Object> resource = new LinkedHashMap<>();
        resource.put("resourceType", "Observation");
        resource.put("status", "final");
        resource.put("code", Map.of("text", entity.getParameter().name().toLowerCase()));
        if (entity.getValue() != null) {
            resource.put("valueQuantity", Map.of("value", entity.getValue(), "unit", entity.getUnit()));
        }
        return post("Observation", resource);
    }

    public String createRiskAssessment(RiskAssessmentEntity entity) {
        Map<String, Object> resource = new LinkedHashMap<>();
        resource.put("resourceType", "RiskAssessment");
        resource.put("status", "final");
        resource.put("code", Map.of("text", "Ergene health risk assessment"));
        resource.put("subject", Map.of("display", entity.getLocationName()));
        resource.put("prediction", Map.of(
                "outcome", Map.of("text", entity.getRiskLevel().name().toLowerCase()),
                "probabilityDecimal", entity.getCarcinogenicRiskAdult()));
        return post("RiskAssessment", resource);
    }

    private String post(String resourceType, Map<String, Object> resource) {
        try {
            String response = restTemplate.postForObject(serverUrl + "/" + resourceType, resource, String.class);
            if (response == null || response.isBlank()) {
                return null;
            }
            JsonNode id = objectMapper.readTree(response).get("id");
            return id == null ? null : id.asText();
        } catch (RestClientException | java.io.IOException exception) {
            return null;
        }
    }
}
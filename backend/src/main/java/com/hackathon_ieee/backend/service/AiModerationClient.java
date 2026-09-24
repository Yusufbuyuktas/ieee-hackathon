package com.hackathon_ieee.backend.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiModerationClient {
    private final RestTemplate restTemplate;

    @Value("${ai.service-url:http://localhost:8000}")
    private String serviceUrl; // backend’in AI servisine ulaştığı iç Docker adresidir.

    @Value("${storage.public-base-url:http://localhost:8080}")
    private String publicBaseUrl; // fotoğrafın internette veya ağ üzerinde erişilebildiği backend adresidir

    public Result moderate(String photoFilename, String category) {
        Map<String, String> request = Map.of(
                "photo_url", publicBaseUrl + "/uploads/" + photoFilename,
                "category", category
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            Response response = restTemplate.postForObject(
                    serviceUrl + "/moderate-photo",
                    new HttpEntity<>(request, headers),
                    Response.class
            );
            if (response == null) {
                throw new RestClientException("AI returned an empty response");
            }
            return new Result(response.tutarli, response.guvenSkoru, response.aciklama, response.moderationStatus);
        } catch (RestClientException exception) {
            return null;
        }
    }

    public record Result(boolean tutarli, double guvenSkoru, String aciklama, String moderationStatus) {
    }

    private static class Response {
        private boolean tutarli;
        @JsonProperty("guven_skoru")
        private double guvenSkoru;
        private String aciklama;
        @JsonProperty("moderation_status")
        private String moderationStatus;

        public boolean isTutarli() {
            return tutarli;
        }

        public double getGuvenSkoru() {
            return guvenSkoru;
        }

        public String getAciklama() {
            return aciklama;
        }

        public String getModerationStatus() {
            return moderationStatus;
        }
    }
}

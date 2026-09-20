package com.hackathon_ieee.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ObservationResponse { // observationcontroller içerisinde bulunan /api/observations "post" endpoint'ine bir kayıt gönderdiğimizde dönen cevap.
    // kayıt eklendikten sonra dönüyor sadece.
    private String id;
    private String fhirObservationId;
    private boolean riskFlagged;
    private List<String> exceededStandards;
}
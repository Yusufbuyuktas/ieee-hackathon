package com.hackathon_ieee.backend.model;

import com.hackathon_ieee.backend.enums.RiskLevel;
import com.hackathon_ieee.backend.enums.SourceType;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 * api-contract.md'deki "Saglik Riski Degerlendirmesi Gonderme" (POST /api/risk-assessments)
 * uc noktasinin arkasindaki veri modeli. Aydin ve ark. (2026) Tablo 9'daki hazir
 * kanserojen risk / THI degerlerini tasimak icin tasarlandi - risk skoru burada
 * HESAPLANMAZ, kaynaktan oldugu gibi alinir.
 */


@Getter
@Setter
@Entity
@Table(name = "risk_assessments")
public class RiskAssessmentEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id; // orn. "ERG-2025-ST2-HRA"

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(name = "station_no")
    private Integer stationNo;

    @Column(name = "assessed_at", nullable = false)
    private OffsetDateTime assessedAt;

    @Column(name = "carcinogenic_risk_child")
    private Double carcinogenicRiskChild;

    @Column(name = "carcinogenic_risk_adult")
    private Double carcinogenicRiskAdult;

    @Column(name = "total_hazard_index_child")
    private Double totalHazardIndexChild;

    @Column(name = "total_hazard_index_adult")
    private Double totalHazardIndexAdult;

    /** Servis katmani hesaplar (orn. carcinogenicRisk > 1.0 ise HIGH). Entity hesaplamaz. */
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;

    @Column(name = "basis_note", columnDefinition = "TEXT")
    private String basisNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(name = "citation", columnDefinition = "TEXT")
    private String citation;

    @Column(name = "fhir_risk_assessment_id")
    private String fhirRiskAssessmentId;

    protected RiskAssessmentEntity() {
        // JPA icin gerekli
    }

    public RiskAssessmentEntity(String id, String locationName, OffsetDateTime assessedAt, SourceType sourceType) {
        this.id = id;
        this.locationName = locationName;
        this.assessedAt = assessedAt;
        this.sourceType = sourceType;
    }
}

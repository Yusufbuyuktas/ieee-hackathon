package com.hackathon_ieee.backend.model;

import com.hackathon_ieee.backend.enums.AiValidationStatus;
import com.hackathon_ieee.backend.enums.CitizenReportCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Getter
@Setter
@Entity
@Table(name = "citizen_reports")
public class CitizenReportEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CitizenReportCategory category;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "report_timestamp", nullable = false)
    private String timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_validation_status", nullable = false)
    private AiValidationStatus aiValidationStatus;

    @Column(name = "ai_confidence")
    private Double aiConfidence;

    @Column(name = "ai_explanation", columnDefinition = "TEXT")
    private String aiExplanation;

    @Column(name = "ai_model")
    private String aiModel;

    @Column(name = "fhir_observation_id")
    private String fhirObservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    protected CitizenReportEntity() {
    }

    public CitizenReportEntity(String id, String photoUrl, CitizenReportCategory category,
                               String note, Double latitude, Double longitude, String timestamp) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.category = category;
        this.note = note;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
}

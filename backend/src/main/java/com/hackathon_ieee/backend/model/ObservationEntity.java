package com.hackathon_ieee.backend.model;

import com.hackathon_ieee.backend.enums.Parameter;
import com.hackathon_ieee.backend.enums.SampleType;
import com.hackathon_ieee.backend.enums.SourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * api-contract.md'deki "Gozlem Gonderme" (POST /api/observations) ve
 * "Gozlemleri Listeleme" (GET /api/observations) uc noktalarinin arkasindaki veri modeli.

 * Düz kolon olarak modellenmeyen alanlar (well_depth_m, water_quality, coordinate_source
 * dışındaki ekstra detaylar) extraDataJson icinde saklanir - servis katmani Jackson
 * ObjectMapper ile serialize/deserialize eder. Bu, JSON'un esnekligini korurken
 * çekirdek alanlarin (parameter, value, timestamp vb.) sorgulanabilir/tipli kalmasini saglar.
 */

@Getter
@Setter
@Entity
@Table(name = "observations")
public class ObservationEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id; // orn. "ERG-2025-ST1-AS-WAT" (measurement_id)

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(name = "station_no")
    private Integer stationNo;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    /** orn. "approximated_from_figure". Null ise hassas/gercek kabul edilir. */
    @Column(name = "coordinate_source")
    private String coordinateSource;

    @Column(name = "observed_at", nullable = false)
    private OffsetDateTime observedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "parameter", nullable = false)
    private Parameter parameter;

    /** Null ise tespit limitinin altinda - bkz. belowDetectionLimit. SIFIR degildir. */
    @Column(name = "value")
    private Double value;

    @Column(name = "below_detection_limit", nullable = false)
    private boolean belowDetectionLimit;

    /** "mg/L" veya "mg/kg" - sampleType.getExpectedUnit() ile eslesmeli, servis dogrular. */
    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "method")
    private String method;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "sample_type", nullable = false)
    private SampleType sampleType;

    @Column(name = "citation", columnDefinition = "TEXT")
    private String citation;

    @Column(name = "risk_flagged", nullable = false)
    private boolean riskFlagged;

    /**
     * Aşılan standartlarin virgulle ayrilmis listesi (orn. "TS_2005,WHO_2006").
     * SADECE sampleType su ise ve ilgili parametre için ThresholdConfig'te doğrulanmış
     * eşik varsa doldurulur. Sediman icin ve esigi henuz dogrulanmamis parametreler
     * (arsenic, nickel, manganese) için boş kalır - servis katmani bunu atlar, uydurmaz.
     */
    @Column(name = "exceeded_standards", columnDefinition = "TEXT")
    private String exceededStandardsCsv;

    /** HAPI FHIR sunucusuna basariyla yazildiktan sonra donen kaynak id'si. */
    @Column(name = "fhir_observation_id")
    private String fhirObservationId;

    @Column(name = "extra_data", columnDefinition = "TEXT")
    private String extraDataJson;

    protected ObservationEntity() {
        // JPA icin gerekli
    }

    public ObservationEntity(String id, String locationName, Parameter parameter, SourceType sourceType, SampleType sampleType, OffsetDateTime observedAt, String unit) {
        this.id = id;
        this.locationName = locationName;
        this.parameter = parameter;
        this.sourceType = sourceType;
        this.sampleType = sampleType;
        this.observedAt = observedAt;
        this.unit = unit;
        this.belowDetectionLimit = false;
        this.riskFlagged = false;
    }

}
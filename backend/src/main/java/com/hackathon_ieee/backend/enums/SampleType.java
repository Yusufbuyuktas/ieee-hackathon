package com.hackathon_ieee.backend.enums;

/**
 * Örnek türü. Her türün kendi beklenen birimi vardir - bu, ayni parametrenin
 * (orn. chromium) su örneğinde mg/L, sediman orneginde mg/kg gelebilmesinden dolayi
 * gerekli. Servis katmanı, gelen "unit" değerini bununla dogrulamali.
 */
public enum SampleType {
    GROUNDWATER("mg/L"),
    SURFACE_WATER("mg/L"),
    SEDIMENT("mg/kg");

    private final String expectedUnit;

    SampleType(String expectedUnit) {
        this.expectedUnit = expectedUnit;
    }

    public String getExpectedUnit() {
        return expectedUnit;
    }

    /**
     * ONEMLI: ThresholdConfig'teki esik degerleri (TS/WHO/EPA icme suyu standartlari)
     * SADECE su orneklerine uygulanabilir. Sediman icin bu standartlar gecerli degil -
     * ayri bir degerlendirme cercevesi (CF/CD/PLI, Hakanson 1980) gerekir ve bu proje
     * kapsaminda henuz uygulanmamistir. Servis katmani, sample_type SEDIMENT ise
     * ThresholdConfig karsilastirmasini atlamali.
     */
    public boolean supportsDrinkingWaterThresholds() {
        return this == GROUNDWATER || this == SURFACE_WATER;
    }
}

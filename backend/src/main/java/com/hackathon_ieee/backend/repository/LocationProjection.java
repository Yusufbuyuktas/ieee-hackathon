package com.hackathon_ieee.backend.repository;

import com.hackathon_ieee.backend.enums.SampleType;

public interface LocationProjection {
    String getLocationName();

    Integer getStationNo();

    SampleType getSampleType();

    Double getLatitude();

    Double getLongitude();
}
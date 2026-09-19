package com.hackathon_ieee.backend.service;

import com.hackathon_ieee.backend.dto.CoordinatesDto;
import com.hackathon_ieee.backend.dto.LocationListItemDto;
import com.hackathon_ieee.backend.repository.LocationProjection;
import com.hackathon_ieee.backend.repository.ObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final ObservationRepository observationRepository;

    public List<LocationListItemDto> findAll() {
        Map<String, LocationAccumulator> locations = new LinkedHashMap<>();

        for (LocationProjection row : observationRepository.findDistinctLocations()) {
            String key = row.getLocationName() + "\u0000" + row.getStationNo();

            LocationAccumulator location = locations.computeIfAbsent(key,ignored -> new LocationAccumulator(row));
            location.sampleTypes.add(row.getSampleType().name().toLowerCase());
        }
        return locations.values().stream().map(LocationAccumulator::toDto).toList();
    }

    private static class LocationAccumulator {
        private final String locationName;
        private final Integer stationNo;
        private final CoordinatesDto coordinates;
        private final List<String> sampleTypes = new ArrayList<>();

        private LocationAccumulator(LocationProjection row) {
            this.locationName = row.getLocationName();
            this.stationNo = row.getStationNo();
            this.coordinates = row.getLatitude() == null || row.getLongitude() == null ? null : coordinates(row);
        }

        private LocationListItemDto toDto() {
            return new LocationListItemDto(locationName, stationNo, sampleTypes, coordinates);
        }

        private static CoordinatesDto coordinates(LocationProjection row) {
            CoordinatesDto coordinates = new CoordinatesDto();
            coordinates.setLat(row.getLatitude());
            coordinates.setLon(row.getLongitude());
            return coordinates;
        }
    }
}
package com.hackathon_ieee.backend.controller;

import com.hackathon_ieee.backend.dto.LocationListResponse;
import com.hackathon_ieee.backend.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService service;

    @GetMapping
    public LocationListResponse findAll() {
        return new LocationListResponse(service.findAll());
    }
}
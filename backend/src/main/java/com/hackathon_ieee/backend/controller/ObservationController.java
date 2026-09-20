package com.hackathon_ieee.backend.controller;

import com.hackathon_ieee.backend.dto.ObservationCreateRequest;
import com.hackathon_ieee.backend.dto.ObservationListResponse;
import com.hackathon_ieee.backend.dto.ObservationResponse;
import com.hackathon_ieee.backend.service.ObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/observations")
@RequiredArgsConstructor
public class ObservationController {
    private final ObservationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObservationResponse create(@RequestBody ObservationCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public ObservationListResponse findAll(
            @RequestParam(required = false) String parameter,
            @RequestParam(name = "from", required = false) String from,
            @RequestParam(name = "to", required = false) String to) {
        return new ObservationListResponse(service.findAll(parameter, parseFrom(from), parseTo(to)));
    }

    private OffsetDateTime parseFrom(String value) {
        return parse(value, false);
    }

    private OffsetDateTime parseTo(String value) {
        return parse(value, true);
    }

    private OffsetDateTime parse(String value, boolean endOfDay) {
        if (value == null || value.isBlank()) return null;
        try {
            return OffsetDateTime.parse(value);
        } catch (java.time.format.DateTimeParseException exception) {
            try {
                return LocalDate.parse(value).atTime(endOfDay ? java.time.LocalTime.MAX : java.time.LocalTime.MIN)
                        .atOffset(ZoneOffset.UTC);
            } catch (java.time.format.DateTimeParseException invalidDate) {
                throw new IllegalArgumentException("Invalid date filter: " + value, invalidDate);
            }
        }
    }
}
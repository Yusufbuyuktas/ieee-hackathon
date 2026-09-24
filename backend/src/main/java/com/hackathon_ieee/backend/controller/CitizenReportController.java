package com.hackathon_ieee.backend.controller;

import com.hackathon_ieee.backend.dto.CitizenReportResponse;
import com.hackathon_ieee.backend.service.CitizenReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/citizen-reports")
@RequiredArgsConstructor
public class CitizenReportController {
    private final CitizenReportService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CitizenReportResponse create(
            @RequestPart("photo") MultipartFile photo,
            @RequestPart("category") String category,
            @RequestPart(value = "note", required = false) String note,
            @RequestPart(value = "latitude", required = false) Double latitude,
            @RequestPart(value = "longitude", required = false) Double longitude,
            @RequestPart("timestamp") String timestamp) {
        return service.create(photo, category, note, latitude, longitude, timestamp);
    }
}

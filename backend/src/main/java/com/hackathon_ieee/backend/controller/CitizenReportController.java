package com.hackathon_ieee.backend.controller;

import com.hackathon_ieee.backend.dto.CitizenReportResponse;
import com.hackathon_ieee.backend.dto.CitizenReportListResponse;
import com.hackathon_ieee.backend.dto.CitizenReportStatusUpdateRequest;
import com.hackathon_ieee.backend.dto.CitizenReportStatusUpdateResponse;
import com.hackathon_ieee.backend.service.CitizenReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/citizen-reports")
@RequiredArgsConstructor
public class CitizenReportController {

    private static final String SESSION_USER_EMAIL = "USER_EMAIL";

    private final CitizenReportService service;

    @GetMapping
    public CitizenReportListResponse findAll() {
        return new CitizenReportListResponse(service.findAll());
    }

    @GetMapping("/my")
    public CitizenReportListResponse findMine(HttpServletRequest httpRequest) {

        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute(SESSION_USER_EMAIL) == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "not authenticated");
        }

        String userEmail = session.getAttribute(SESSION_USER_EMAIL).toString();

        return new CitizenReportListResponse(
                service.findMine(userEmail));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CitizenReportResponse create(
            @RequestPart("photo") MultipartFile photo,
            @RequestParam("category") String category,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam("timestamp") String timestamp,
            HttpServletRequest httpRequest) {

        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute(SESSION_USER_EMAIL) == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "not authenticated");
        }

        String userEmail = session.getAttribute(SESSION_USER_EMAIL).toString();

        return service.create(photo,category,note,latitude,longitude,timestamp,userEmail);
    }

    @PatchMapping("/{id}/status")
    public CitizenReportStatusUpdateResponse updateStatus(
            @PathVariable String id,
            @RequestBody CitizenReportStatusUpdateRequest request) {

        return service.updateStatus(id, request.status());
    }
}
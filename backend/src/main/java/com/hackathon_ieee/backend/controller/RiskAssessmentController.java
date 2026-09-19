package com.hackathon_ieee.backend.controller;

import com.hackathon_ieee.backend.dto.RiskAssessmentCreateRequest;
import com.hackathon_ieee.backend.dto.RiskAssessmentResponse;
import com.hackathon_ieee.backend.dto.RiskAssessmentListResponse;
import com.hackathon_ieee.backend.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/risk-assessments")
@RequiredArgsConstructor
public class RiskAssessmentController {
    private final RiskAssessmentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RiskAssessmentResponse create(@RequestBody RiskAssessmentCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public RiskAssessmentListResponse findAll(@RequestParam(required = false) String location) {
        return new RiskAssessmentListResponse(service.findAll(location));
    }
}
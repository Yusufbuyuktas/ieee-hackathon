package com.hackathon_ieee.backend.dto;

public record LoginRequest(
        String email,
        String password
) {
}
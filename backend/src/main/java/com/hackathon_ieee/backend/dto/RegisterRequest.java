package com.hackathon_ieee.backend.dto;

public record RegisterRequest(
        String fullName,
        String email,
        String password
) {
}
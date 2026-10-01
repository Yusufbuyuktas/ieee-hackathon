package com.hackathon_ieee.backend.dto;

import com.hackathon_ieee.backend.enums.UserRole;

public record AuthUserResponse(
        String id,
        String fullName,
        String email,
        UserRole role
) {
}
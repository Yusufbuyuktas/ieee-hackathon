package com.hackathon_ieee.backend.service;

import com.hackathon_ieee.backend.dto.AuthUserResponse;
import com.hackathon_ieee.backend.dto.RegisterRequest;
import com.hackathon_ieee.backend.enums.UserRole;
import com.hackathon_ieee.backend.model.UserEntity;
import com.hackathon_ieee.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthUserResponse register(RegisterRequest request) {

        if (request.fullName() == null || request.fullName().isBlank()
                || request.email() == null || request.email().isBlank()
                || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "fullName, email and password are required");
        }

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "email is already registered");
        }

        UserEntity user = new UserEntity();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.CITIZEN);

        user = userRepository.save(user);

        return toResponse(user);
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "invalid email or password"));
    }

    public AuthUserResponse toResponse(UserEntity user) {
        return new AuthUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole());
    }
}
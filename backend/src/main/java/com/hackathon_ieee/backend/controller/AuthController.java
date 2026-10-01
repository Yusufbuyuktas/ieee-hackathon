package com.hackathon_ieee.backend.controller;

import com.hackathon_ieee.backend.dto.AuthMessageResponse;
import com.hackathon_ieee.backend.dto.AuthUserResponse;
import com.hackathon_ieee.backend.dto.LoginRequest;
import com.hackathon_ieee.backend.dto.RegisterRequest;
import com.hackathon_ieee.backend.model.UserEntity;
import com.hackathon_ieee.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String SESSION_USER_ID = "USER_ID";
    private static final String SESSION_USER_EMAIL = "USER_EMAIL";
    private static final String SESSION_USER_ROLE = "USER_ROLE";

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthUserResponse register(
            @RequestBody RegisterRequest request) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthUserResponse login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        if (request.email() == null || request.email().isBlank()
                || request.password() == null || request.password().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "email and password are required");
        }

        UserEntity user = authService.findByEmail(request.email());

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword())) {

            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "invalid email or password");
        }

        HttpSession oldSession = httpRequest.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session = httpRequest.getSession(true);

        session.setAttribute(SESSION_USER_ID, user.getId());
        session.setAttribute(SESSION_USER_EMAIL, user.getEmail());
        session.setAttribute(SESSION_USER_ROLE, user.getRole().name());

        var authority = new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().name());

        var authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(authority));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);

        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext);

        return authService.toResponse(user);
    }

    @GetMapping("/me")
    public AuthUserResponse me(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "not authenticated");
        }

        Object email = session.getAttribute(SESSION_USER_EMAIL);

        if (email == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "not authenticated");
        }

        UserEntity user = authService.findByEmail(email.toString());

        return authService.toResponse(user);
    }

    @PostMapping("/logout")
    public AuthMessageResponse logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return new AuthMessageResponse("logged out");
    }
}
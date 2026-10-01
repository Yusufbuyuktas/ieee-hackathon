package com.hackathon_ieee.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Belediye personeli citizen report durumunu değiştirebilir
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/citizen-reports/*/status")
                        .hasRole("MUNICIPALITY_STAFF")

                        // Klinik gözlem verileri yalnızca doktor tarafından görüntülenebilir
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/observations/**")
                        .hasRole("DOCTOR")

                        // Klinik risk değerlendirmeleri yalnızca doktor tarafından görüntülenebilir
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/risk-assessments/**")
                        .hasRole("DOCTOR")

                        .anyRequest().permitAll());

        return http.build();
    }
}
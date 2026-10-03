package com.hackathon_ieee.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. WebConfig'deki CORS kurallarını bağlar
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // 2. Tarayıcı Preflight (OPTIONS) istekleri
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 3. Kimlik doğrulama ve yüklenen görseller
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // 4. Tüm GET veri okuma servisleri (Jüri sunumunda oturum düşse dahi 403 almaz)
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/observations",
                                "/api/observations/**")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/locations",
                                "/api/locations/**")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/risk-assessments",
                                "/api/risk-assessments/**")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/citizen-reports",
                                "/api/citizen-reports/**")
                        .permitAll()

                        // 5. Vatandaş raporu durum onaylama/reddetme (Yalnızca belediye personeli)
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/citizen-reports/*/status")
                        .hasRole("MUNICIPALITY_STAFF")

                        // 6. Kalan tüm yazma/güncelleme/silme işlemleri oturum gerektirir
                        .anyRequest().authenticated());

        return http.build();
    }
}
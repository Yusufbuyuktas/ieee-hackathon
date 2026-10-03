package com.hackathon_ieee.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
                // 1. WebConfig'deki CORS kurallarını Spring Security zincirine bağlar
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // 2. Tarayıcının gönderdiği OPTIONS (Preflight) isteklerini serbest bırakır
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 3. Login, Register, Me vb. auth işlemlerine izin verir
                        .requestMatchers("/api/auth/**").permitAll()

                        // 4. Belediye personeli citizen report durumunu değiştirebilir
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/citizen-reports/*/status")
                        .hasRole("MUNICIPALITY_STAFF")

                        // 5. Çevresel gözlem telemetrileri hem doktor hem belediye personeline açıktır
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/observations",
                                "/api/observations/**")
                        .hasAnyRole("DOCTOR", "MUNICIPALITY_STAFF")

                        // 6. Klinik risk değerlendirmeleri yalnızca doktora açıktır
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/risk-assessments/**")
                        .hasRole("DOCTOR")

                        .anyRequest().permitAll());

        return http.build();
    }
}
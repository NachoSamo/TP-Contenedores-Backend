package com.tpi.backend.msflota.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // ADMINS pueden crear entidades (camiones, transportistas, tarifas)
                        .requestMatchers(HttpMethod.POST, "/api/flota/camiones", "/api/flota/transportistas", "/api/flota/tarifas").hasRole("ADMIN")

                        // USERS pueden listar todo y realizar cálculos
                        .requestMatchers(HttpMethod.GET, "/api/flota/**").hasAnyRole("USER", "ADMIN")

                        // Cualquier otra petición debe estar autenticada
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter())
                        )
                );
        return http.build();
    }
}
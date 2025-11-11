package com.tpi.backend.mssolicitudes.security;

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
                        // ADMINS pueden crear datos maestros (clientes, contenedores, estados)
                        .requestMatchers(HttpMethod.POST, "/api/solicitudes/clientes", "/api/solicitudes/contenedores", "/api/solicitudes/estados").hasRole("ADMIN")

                        // USERS pueden crear una nueva solicitud de transporte
                        .requestMatchers(HttpMethod.POST, "/api/solicitudes").hasAnyRole("USER", "ADMIN")

                        // USERS pueden listar toda la información
                        .requestMatchers(HttpMethod.GET, "/api/solicitudes/**").hasAnyRole("USER", "ADMIN")

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
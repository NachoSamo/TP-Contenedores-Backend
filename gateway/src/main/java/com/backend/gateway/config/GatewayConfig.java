package com.backend.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator configureRoutes(RouteLocatorBuilder builder,
                                        @Value("${gateway.url-ms-solicitudes}") String uriSolicitudes,
                                        @Value("${gateway.url-ms-rutas}") String uriRutas,
                                        @Value("${gateway.url-ms-flota}") String uriFlota) {

        return builder.routes()
                // 🔹 Microservicio de Solicitudes
                .route("solicitudes", r -> r.path("/api/solicitudes/**")
                        .filters(f -> f.addRequestHeader("X-Gateway", "UTN-Gateway"))
                        .uri(uriSolicitudes))

                // 🔹 Microservicio de Rutas
                .route("rutas", r -> r.path("/api/rutas/**")
                        .filters(f -> f.rewritePath("/api/rutas/(?<segment>.*)", "/${segment}"))
                        .uri(uriRutas))

                // 🔹 Microservicio de Flota
                .route("flota", r -> r.path("/api/flota/**")
                        .filters(f -> f.addRequestHeader("X-Microservice", "flota"))
                        .uri(uriFlota))

                .build();
    }
}

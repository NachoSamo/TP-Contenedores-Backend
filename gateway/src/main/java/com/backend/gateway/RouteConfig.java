package com.backend.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("solicitudes", r -> r.path("/api/solicitudes/**")
                .filters(f -> f.addRequestHeader("X-Gateway", "true"))
                .uri("http://ms-solicitudes:8083"))
            .route("rutas", r -> r.path("/api/rutas/**")
                .uri("http://ms-rutas:8082"))
            .route("flota", r -> r.path("/api/flota/**")
                .uri("http://ms-flota:8085"))
            .build();
    }
}

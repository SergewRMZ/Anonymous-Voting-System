package com.voting_system.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class GatewayConfig {
    @Bean 
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("authorization-service", r -> r
                .path("/api/elections/**")
                .uri("http://localhost:8080")
            )
            .route("authentication-service", r -> r
                .path("/api/auth/**")
                .uri("http://localhost:8082")
            )
            .route("election-service", r -> r
                .path("/api/election-service/**")
                .uri("http://localhost:8083")

            )
            .route("tally-service", r -> r
                .path("/api/tally-service/election/**")
                .uri("http://localhost:8084")
            )
            .build();
    }
}
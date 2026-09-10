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
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8080")
            ).build();
    }
}
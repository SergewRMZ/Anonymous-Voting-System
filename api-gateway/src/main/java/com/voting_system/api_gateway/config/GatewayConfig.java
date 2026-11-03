package com.voting_system.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator routeLocator(
        RouteLocatorBuilder builder,
        @Value("${services.authorization-service.base-url}") String authorizationServiceUrl,
        @Value("${services.authentication-service.base-url}") String authenticationServiceUrl,
        @Value("${services.election-service.base-url}") String electionServiceUrl,
        @Value("${services.tally-service.base-url}") String tallyServiceUrl,
        @Value("${services.bulletin-board.base-url}") String bulletinBoardUrl
    ) {
        return builder.routes()
            .route("authorization-service", r -> r
                .path("/api/authorization-service/**")
                .uri(authorizationServiceUrl)
            )

            .route("authentication-service", r -> r
                .path("/api/auth/**")
                .uri(authenticationServiceUrl)
            )

            .route("election-service", r -> r
                .path("/api/election-service/**")
                .uri(electionServiceUrl)

            )

            .route("tally-service", r -> r
                .path("/api/tally-service/**")
                .uri(tallyServiceUrl)
            )

            .route("bulletin-board", r -> r
                .path("/api/bulletin-board/**")
                .uri(bulletinBoardUrl)
            )
            .build();
    }
}

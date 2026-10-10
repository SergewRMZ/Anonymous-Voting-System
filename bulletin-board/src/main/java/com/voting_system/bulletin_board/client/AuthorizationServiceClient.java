package com.voting_system.bulletin_board.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class AuthorizationServiceClient {
    private final WebClient.Builder webClientBuilder;

    @Value ("${services.authorization-service.base-url}")
    private String apiGatewayBaseUrl;

    public AuthorizationPublicKey getPublicKey(UUID electionId) {
        return this.webClientBuilder.build()
            .get()
            .uri(apiGatewayBaseUrl + "/api/authorization-service/elections/{electionId}/keys/public", electionId)
            .retrieve()
            .bodyToMono(AuthorizationPublicKey.class)
            .block();
    }

}

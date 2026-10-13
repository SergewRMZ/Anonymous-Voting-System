package com.voting_system.tally_service.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ElectionServiceClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${services.election-service.base-url}")
    private String electionServiceBaseUrl;

    @Value("${services.internal.token}")
    private String internalToken;

    public ElectionStatusResponse getStatus(UUID electionId) {
        return webClientBuilder.build()
            .get()
            .uri(electionServiceBaseUrl + "/internal/elections/{electionId}/status", electionId)
            .header("X-Internal-Token", internalToken)
            .retrieve()
            .bodyToMono(ElectionStatusResponse.class)
            .block();
    }

}

package com.voting_system.bulletin_board.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ElectionServiceClient {
    private final WebClient.Builder webClientBuilder;

    @Value ("${services.election-service.base-url}")
    private String electionServiceBaseUrl;

    public GetElectionStatusDto getElectionStatus(UUID electionId) {
        return this.webClientBuilder.build()
            .get()
            .uri(electionServiceBaseUrl + "/api/election-service/elections/{electionId}/state", electionId)
            .retrieve()
            .bodyToMono(GetElectionStatusDto.class)
            .block();
    }
}

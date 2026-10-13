package com.voting_system.election_service.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BulletinBoardClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${services.bulletin-board.base-url}")
    private String bulletinBoardBaseUrl;

    @Value("${services.internal.token}")
    private String internalToken;

    public void closeVoteAdmission(UUID electionId) {
        webClientBuilder.build()
            .post()
            .uri(bulletinBoardBaseUrl + "/internal/elections/{electionId}/close", electionId)
            .header("X-Internal-Token", internalToken)
            .retrieve()
            .toBodilessEntity()
            .block();
    }
}

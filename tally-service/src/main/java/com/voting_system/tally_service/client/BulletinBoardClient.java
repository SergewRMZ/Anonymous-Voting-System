package com.voting_system.tally_service.client;

import java.util.List;
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

    public List<EncryptedVoteDto> getEncryptedVotes(UUID electionId) {
        return webClientBuilder.build()
            .get()
            .uri(bulletinBoardBaseUrl + "/internal/elections/{electionId}/votes", electionId)
            .header("X-Internal-Token", internalToken)
            .retrieve()
            .bodyToFlux(EncryptedVoteDto.class)
            .collectList()
            .block();
    }

    public record EncryptedVoteDto(UUID authorizationKeysId, List<PositionDto> positions) {}
    public record PositionDto(UUID electionPositionId, List<BallotDto> ballots) {}
    public record BallotDto(UUID candidacyId, CiphertextDto vote) {}
    public record CiphertextDto(String c1, String c2) {}
}

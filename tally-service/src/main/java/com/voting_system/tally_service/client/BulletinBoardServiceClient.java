package com.voting_system.tally_service.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.voting_system.tally_service.client.dto.PageResponseDto;
import com.voting_system.tally_service.client.dto.VoteBallotDto;

@Component 
public class BulletinBoardServiceClient {
    private final WebClient webClient;
    
    public BulletinBoardServiceClient(
        WebClient.Builder builder,
        @Value("${services.bulletin-board-service.base-url}") String bulletinBoardServiceBaseUrl
    ) {

        this.webClient = builder
            .baseUrl(bulletinBoardServiceBaseUrl)
            .build();
    }

    public PageResponseDto<VoteBallotDto> getBallots(
        UUID electionId, 
        UUID electionPositionId, 
        UUID candidacyId,
        int page,
        int size
    ) {
        return this.webClient
            .get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/bulletin-board/elections/{electionId}/positions/{electionPositionId}/candidacies/{candidacyId}/ballots")
                .queryParam("page", page)
                .queryParam("size", size)
                .build(electionId, electionPositionId, candidacyId)
            )
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<
                PageResponseDto<VoteBallotDto>
                >() {}
            )
            .block();
    }
}

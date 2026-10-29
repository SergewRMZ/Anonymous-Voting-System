package com.voting_system.tally_service.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.voting_system.tally_service.client.dto.ElectionDtoResponse;
import com.voting_system.tally_service.client.dto.GetElectionStatusDto;
import com.voting_system.tally_service.client.dto.GetElectionStructureDto;

@Component 
public class ElectionServiceClient {
    private final WebClient webClient;
    public ElectionServiceClient(
        WebClient.Builder builder,
        @Value ("${services.election-service.base-url}") String electionServiceBaseUrl
    ) {

        this.webClient = builder
            .baseUrl(electionServiceBaseUrl)
            .build();
    }

    public GetElectionStatusDto getElectionStatus(UUID electionId) {
        return this.webClient.get()
            .uri("/api/election-service/elections/{electionId}/state", electionId)
            .retrieve()
            .bodyToMono(GetElectionStatusDto.class)
            .block();
    }

    public GetElectionStructureDto getElectionStructureDto(UUID electionId) {
        return this.webClient.get()
            .uri("/api/election-service/elections/{electionId}/structure", electionId)
            .retrieve()
            .bodyToMono(GetElectionStructureDto.class)
            .block();
    }

    public ElectionDtoResponse finishElection(UUID electionId) {
        return this.webClient.patch()
            .uri("/api/election-service/elections/{electionId}/finish", electionId)
            .retrieve()
            .bodyToMono(ElectionDtoResponse.class)
            .block();
    }
}

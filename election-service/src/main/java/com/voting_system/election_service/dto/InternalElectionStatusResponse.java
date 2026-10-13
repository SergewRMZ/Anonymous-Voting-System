package com.voting_system.election_service.dto;

import java.time.Instant;
import java.util.UUID;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.domain.ElectionStatus;

public record InternalElectionStatusResponse(
    UUID electionId,
    ElectionStatus status,
    boolean acceptingVotes
) {
    public static InternalElectionStatusResponse from(ElectionModel election, Instant now) {
        return new InternalElectionStatusResponse(
            election.getId(),
            election.getStatus(),
            election.canAcceptVotes(now)
        );
    }
}

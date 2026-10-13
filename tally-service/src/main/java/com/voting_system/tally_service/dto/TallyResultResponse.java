package com.voting_system.tally_service.dto;

import java.util.List;
import java.util.UUID;

public record TallyResultResponse(
    UUID electionId,
    int ballotsProcessed,
    List<CandidateTally> results
) {
    public record CandidateTally(
        UUID electionPositionId,
        UUID candidacyId,
        int votes
    ) {}
}

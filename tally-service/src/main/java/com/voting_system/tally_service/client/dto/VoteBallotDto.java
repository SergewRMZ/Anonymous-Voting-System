package com.voting_system.tally_service.client.dto;
import java.util.UUID;

public record VoteBallotDto(
    UUID id,
    UUID electionId,
    UUID electionPositionId,
    UUID candidacyId,
    String c1,
    String c2
) {}


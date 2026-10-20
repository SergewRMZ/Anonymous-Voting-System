package com.voting_system.tally_service.client.dto;

import java.util.UUID;

public record GetElectionStatusDto (
    UUID electionId,
    ElectionStatus status
) {}

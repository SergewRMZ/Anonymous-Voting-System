package com.voting_system.election_service.dto;

import java.util.List;
import java.util.UUID;

public record CreateCandidacyDtoRequest(
    List<UUID> candidateIds
) {}

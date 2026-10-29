package com.voting_system.tally_service.client.dto;

import java.time.Instant;
import java.util.UUID;

public record ElectionDtoResponse(
    UUID electionId,
    String name,
    Instant startDate,
    Instant endDate,
    ElectionStatus status
) {}
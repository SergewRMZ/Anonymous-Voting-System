package com.voting_system.election_service.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.voting_system.election_service.domain.ElectionStatus;

public record ElectionDetailsDtoResponse(
    UUID electionId,
    String name,
    String description,
    Instant startDate,
    Instant endDate,
    ElectionStatus status,
    List<ElectionPositionDetailsDtoResponse> positions
) {}

package com.voting_system.election_service.dto;

import java.util.List;
import java.util.UUID;

public record ElectionPositionDetailsDtoResponse(
    UUID electionPositionId,
    UUID positionId,
    String positionName,
    List<ElectionCandidacyDtoResponse> candidacies
) {}

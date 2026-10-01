package com.voting_system.election_service.dto;

import java.time.Instant;
import java.util.UUID;

import com.voting_system.election_service.domain.CandidateModel;

public record ElectionCandidateDtoResponse(
    UUID candidateId,
    String name,
    String lastName,
    String description,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt
) {
    public static ElectionCandidateDtoResponse fromModel(CandidateModel model) {
        return new ElectionCandidateDtoResponse(
            model.getId(),
            model.getName(),
            model.getLastName(),
            model.getDescription(),
            model.isActive(),
            model.getCreatedAt(),
            model.getUpdatedAt()
        );
    }
}

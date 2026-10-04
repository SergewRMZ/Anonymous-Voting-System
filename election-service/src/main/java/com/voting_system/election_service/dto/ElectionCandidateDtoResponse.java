package com.voting_system.election_service.dto;

import java.time.Instant;
import java.util.UUID;

import com.voting_system.election_service.domain.CandidacyModel;
import com.voting_system.election_service.domain.CandidateModel;

public record ElectionCandidateDtoResponse(
    UUID candidacyId,
    UUID candidateId,
    String name,
    String lastName,
    String description,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt
) {
    public static ElectionCandidateDtoResponse fromModels(
        CandidacyModel candidacy, 
        CandidateModel model
    ) {
        
        return new ElectionCandidateDtoResponse(
            candidacy.getId(),
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

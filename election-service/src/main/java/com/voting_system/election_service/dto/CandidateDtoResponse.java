package com.voting_system.election_service.dto;

import java.time.Instant;
import java.util.UUID;

import com.voting_system.election_service.domain.CandidateModel;

public record CandidateDtoResponse(
    UUID id,
    String name,
    String lastName,
    String description,
    Instant createdAt
) {
    public static CandidateDtoResponse fromModel(CandidateModel model) {
        return new CandidateDtoResponse(
            model.getId(), 
            model.getName(), 
            model.getLastName(), 
            model.getDescription(),
            model.getCreatedAt());
    }
}

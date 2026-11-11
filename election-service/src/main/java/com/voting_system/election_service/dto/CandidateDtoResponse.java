package com.voting_system.election_service.dto;
import java.util.UUID;

import com.voting_system.election_service.domain.CandidateModel;

public record CandidateDtoResponse(
    UUID id,
    String name,
    String lastName,
    String description,
    boolean isActive,
    boolean hasPhoto
) {
    public static CandidateDtoResponse fromModel(CandidateModel model) {
        return new CandidateDtoResponse(
            model.getId(), 
            model.getName(), 
            model.getLastName(), 
            model.getDescription(),
            model.isActive(),
            model.isHasPhoto()
        );
    }
}

package com.voting_system.election_service.dto;

import java.util.UUID;

import com.voting_system.election_service.domain.CandidacyModel;
import com.voting_system.election_service.domain.CandidateModel;

public record ElectionCandidacyDtoResponse(
    UUID candidacyId,
    CandidateDtoResponse candidate
) {
    public static ElectionCandidacyDtoResponse fromModels(
        CandidacyModel candidacyModel, 
        CandidateModel candidateModel
    ) {
        
        return new ElectionCandidacyDtoResponse(
            candidacyModel.getId(),
            CandidateDtoResponse.fromModel(candidateModel)
        );
    }
}

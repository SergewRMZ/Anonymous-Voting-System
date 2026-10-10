package com.voting_system.election_service.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.voting_system.election_service.domain.CandidacyModel;
import com.voting_system.election_service.dto.CreateCandidacyDtoRequest;

public interface ICandidacyService {
    public List<CandidacyModel> createCandidacies(UUID electionPositionId, CreateCandidacyDtoRequest request);
}

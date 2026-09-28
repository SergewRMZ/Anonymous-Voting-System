package com.voting_system.election_service.repository.interfaces;

import java.util.List;
import java.util.UUID;

import com.voting_system.election_service.domain.CandidacyModel;

public interface ICandidacyRepository {
    public CandidacyModel save(CandidacyModel model);
    public List<CandidacyModel> saveAll(UUID electionPositionId, List<UUID> candidateIds);
    public List<CandidacyModel> getCandidacies();
}

package com.voting_system.election_service.repository.interfaces;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.voting_system.election_service.domain.CandidacyModel;
import com.voting_system.election_service.domain.CandidateModel;

public interface ICandidacyRepository {
    public CandidacyModel save(CandidacyModel model);
    public List<CandidacyModel> saveAll(UUID electionPositionId, List<UUID> candidateIds);
    public List<CandidacyModel> getCandidacies();
    public Map<UUID, List<CandidateModel>> getCandidatesByElectionPositionIds(List<UUID> electionPositionIds);
}

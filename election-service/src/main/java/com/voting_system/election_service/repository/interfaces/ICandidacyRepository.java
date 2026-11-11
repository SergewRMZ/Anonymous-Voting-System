package com.voting_system.election_service.repository.interfaces;

import java.util.List;
import java.util.UUID;

import com.voting_system.election_service.domain.CandidacyModel;

public interface ICandidacyRepository {
    public CandidacyModel save(CandidacyModel model);
    public List<CandidacyModel> saveAll(UUID electionPositionId, List<UUID> candidateIds);
    public List<CandidacyModel> getCandidacies();
    public List<CandidacyModel> getCandidaciesByElectionPositionIds(List<UUID> electionPositionIds);
    // true si el candidato ya compite en una elección que salió de DRAFT.
    public boolean isCandidateInNonDraftElection(UUID candidateId);
}

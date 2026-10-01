package com.voting_system.election_service.repository.interfaces;

import java.util.List;
import java.util.UUID;

import com.voting_system.election_service.domain.ElectionPositionModel;

public interface IElectionPositionRepository {
    public ElectionPositionModel save(ElectionPositionModel model);
    public List<ElectionPositionModel> getByElectionId(UUID electionId);
}

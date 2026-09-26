package com.voting_system.election_service.repository.interfaces;

import com.voting_system.election_service.domain.ElectionPositionModel;

public interface IElectionPositionRepository {
    public ElectionPositionModel save(ElectionPositionModel model);
}

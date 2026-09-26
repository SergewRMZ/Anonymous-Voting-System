package com.voting_system.election_service.services.interfaces;

import java.util.UUID;

import com.voting_system.election_service.domain.ElectionPositionModel;

public interface IElectionPositionService {
    public ElectionPositionModel associateElectoralPosition(UUID electionId, UUID positionId);
}

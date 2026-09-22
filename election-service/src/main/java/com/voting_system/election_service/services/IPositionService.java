package com.voting_system.election_service.services;

import java.util.UUID;

import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.dto.CreatePositionDtoRequest;

public interface IPositionService {
    public PositionModel createPosition(CreatePositionDtoRequest positionModel, UUID electionId);
}

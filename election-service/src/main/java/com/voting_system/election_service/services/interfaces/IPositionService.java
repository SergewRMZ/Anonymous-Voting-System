package com.voting_system.election_service.services.interfaces;

import java.util.List;

import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.dto.CreatePositionDtoRequest;

public interface IPositionService {
    public PositionModel createPosition(CreatePositionDtoRequest positionModel);
    public List<PositionModel> getPositions();
}

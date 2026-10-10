package com.voting_system.election_service.repository.interfaces;

import java.util.List;
import java.util.UUID;

import com.voting_system.election_service.domain.PositionModel;

public interface IPositionRepository {
    public PositionModel save(PositionModel positionModel);
    public List<PositionModel> getPositions();
    public List<PositionModel> getByIds(List<UUID> ids);
}

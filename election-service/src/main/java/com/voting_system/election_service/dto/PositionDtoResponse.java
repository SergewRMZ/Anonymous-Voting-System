package com.voting_system.election_service.dto;
import java.util.UUID;
import com.voting_system.election_service.domain.PositionModel;

public record PositionDtoResponse(
    UUID id,
    UUID electionId,
    String positionName
) {
    public static PositionDtoResponse from (PositionModel positionModel) {
        return new PositionDtoResponse(
            positionModel.getId(), 
            positionModel.getElectionId(), 
            positionModel.getPositionName());
    }
}

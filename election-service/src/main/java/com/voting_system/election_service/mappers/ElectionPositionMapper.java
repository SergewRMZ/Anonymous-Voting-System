package com.voting_system.election_service.mappers;

import org.springframework.stereotype.Component;

import com.voting_system.election_service.domain.ElectionPositionModel;
import com.voting_system.election_service.entity.JpaElectionEntity;
import com.voting_system.election_service.entity.JpaElectionPositionEntity;
import com.voting_system.election_service.entity.JpaPositionEntity;

@Component 
public class ElectionPositionMapper {
    public JpaElectionPositionEntity toEntity(
        ElectionPositionModel model,
        JpaPositionEntity positionEntity,
        JpaElectionEntity electionEntity
    ) {
        return JpaElectionPositionEntity.builder()
            .id(model.getId())
            .election(electionEntity)
            .position(positionEntity)
            .build();
    }

    public ElectionPositionModel toModel(JpaElectionPositionEntity entity) {
        return ElectionPositionModel.builder()
            .id(entity.getId())
            .electionId(entity.getElection().getId())
            .positionId(entity.getPosition().getId())
            .build();
    }
}

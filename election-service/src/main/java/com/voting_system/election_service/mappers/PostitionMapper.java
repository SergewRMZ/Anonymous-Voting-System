package com.voting_system.election_service.mappers;

import org.springframework.stereotype.Component;

import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.entity.JpaElectionEntity;
import com.voting_system.election_service.entity.JpaPositionEntity;

@Component 
public class PostitionMapper {
    public JpaPositionEntity toEntity(PositionModel model, JpaElectionEntity jpaElectionEntity) {
        return JpaPositionEntity.builder()
            .id(model.getId())
            .electionEntity(jpaElectionEntity)
            .positionName(model.getPositionName())
            .build();
            
    }

    public PositionModel toModel(JpaPositionEntity jpaPositionEntity) {
        return PositionModel.builder()
            .id(jpaPositionEntity.getId())
            .electionId(jpaPositionEntity.getElectionEntity().getId())
            .positionName(jpaPositionEntity.getPositionName())
            .build();
    }
}

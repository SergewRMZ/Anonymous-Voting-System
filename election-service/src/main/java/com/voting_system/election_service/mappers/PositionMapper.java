package com.voting_system.election_service.mappers;

import org.springframework.stereotype.Component;

import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.entity.JpaPositionEntity;

@Component 
public class PositionMapper {
    public JpaPositionEntity toEntity(PositionModel model) {
        return JpaPositionEntity.builder()
            .id(model.getId())
            .positionName(model.getPositionName())
            .build();
            
    }

    public PositionModel toModel(JpaPositionEntity jpaPositionEntity) {
        return PositionModel.builder()
            .id(jpaPositionEntity.getId())
            .positionName(jpaPositionEntity.getPositionName())
            .build();
    }
}

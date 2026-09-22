package com.voting_system.election_service.mappers;

import org.springframework.stereotype.Component;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.entity.JpaElectionEntity;

@Component 
public class ElectionMapper {
    public JpaElectionEntity toEntity(ElectionModel electionModel) {
        if(electionModel == null) return null;
        return JpaElectionEntity.builder()
            .name(electionModel.getName())
            .description(electionModel.getDescription())
            .startsAt(electionModel.getStartDate())
            .endsAt(electionModel.getEndDate())
            .status(electionModel.getStatus())
            .build();
    }

    public ElectionModel toModel(JpaElectionEntity jpaElectionEntity) {
        return ElectionModel.builder()
            .id(jpaElectionEntity.getId())
            .name(jpaElectionEntity.getName())
            .description(jpaElectionEntity.getDescription())
            .startDate(jpaElectionEntity.getStartsAt())
            .endDate(jpaElectionEntity.getEndsAt())
            .status(jpaElectionEntity.getStatus())
            .build();
    }
}

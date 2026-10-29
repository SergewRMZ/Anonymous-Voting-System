package com.voting_system.tally_service.mappers;

import org.springframework.stereotype.Component;

import com.voting_system.tally_service.entity.JpaElectionResultEntity;
import com.voting_system.tally_service.model.ElectionResultModel;

@Component 
public class ElectionResultMapper {
    public JpaElectionResultEntity toEntity(ElectionResultModel model) {
        return JpaElectionResultEntity.builder()
            .id(model.getId())
            .electionId(model.getElectionId())
            .electionPositionId(model.getElectionPositionId())
            .candidacyId(model.getCandidacyId())
            .result(model.getResult())
            .createdAt(model.getCreatedAt())
            .build();
    }

    public ElectionResultModel toModel(JpaElectionResultEntity entity) {
        return ElectionResultModel.builder()
            .id(entity.getId())
            .electionId(entity.getElectionId())
            .electionPositionId(entity.getElectionPositionId())
            .candidacyId(entity.getCandidacyId())
            .result(entity.getResult())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}

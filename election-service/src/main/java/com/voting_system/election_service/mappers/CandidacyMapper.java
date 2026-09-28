package com.voting_system.election_service.mappers;
import org.springframework.stereotype.Component;

import com.voting_system.election_service.domain.CandidacyModel;
import com.voting_system.election_service.entity.JpaCandidacyEntity;
import com.voting_system.election_service.entity.JpaCandidateEntity;
import com.voting_system.election_service.entity.JpaElectionPositionEntity;

@Component 
public class CandidacyMapper {
    public JpaCandidacyEntity toEntity(
        CandidacyModel model,
        JpaCandidateEntity candidateEntity, 
        JpaElectionPositionEntity electionPositionEntity) {
    
            return JpaCandidacyEntity.builder()
                .id(model.getId())
                .electionPositionEntity(electionPositionEntity)
                .candidateEntity(candidateEntity)
                .createdAt(model.getCreatedAt())
                .build();
    }

    public CandidacyModel toModel(JpaCandidacyEntity entity) {
        return CandidacyModel.builder()
            .id(entity.getId())
            .candidateId(entity.getCandidateEntity().getId())
            .electionPositionId(entity.getElectionPositionEntity().getId())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}

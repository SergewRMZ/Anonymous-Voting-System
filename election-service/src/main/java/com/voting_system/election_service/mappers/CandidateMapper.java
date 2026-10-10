package com.voting_system.election_service.mappers;

import org.springframework.stereotype.Component;

import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.entity.JpaCandidateEntity;

@Component 
public class CandidateMapper {
    public JpaCandidateEntity toEntity(CandidateModel model) {
        return JpaCandidateEntity.builder()
            .id(model.getId())
            .name(model.getName())
            .lastName(model.getLastName())
            .description(model.getDescription())
            .isActive(model.isActive())
            .createdAt(model.getCreatedAt())
            .updatedAt(model.getUpdatedAt())
            .build();
    }

    public CandidateModel toModel(JpaCandidateEntity entity) {
        return CandidateModel.builder()
            .id(entity.getId())
            .name(entity.getName())
            .lastName(entity.getLastName())
            .description(entity.getDescription())
            .isActive(entity.isActive())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}

package com.voting_system.election_service.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.dto.CreatePositionDtoRequest;
import com.voting_system.election_service.entity.JpaElectionEntity;
import com.voting_system.election_service.entity.JpaPositionEntity;
import com.voting_system.election_service.repository.IElectionRepository;
import com.voting_system.election_service.repository.IPositionRepository;
import com.voting_system.election_service.exceptions.*;
import com.voting_system.election_service.mappers.PostitionMapper;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PositionService implements IPositionService {
    private final IElectionRepository electionRepository;
    private final IPositionRepository positionRepository;
    private final PostitionMapper postitionMapper;
    @Override 
    public PositionModel createPosition(CreatePositionDtoRequest createPositionDtoRequest, UUID electionId) {
        JpaElectionEntity jpaElectionEntity = electionRepository
            .findById(electionId)
            .orElseThrow(() -> new ElectionNotFoundException("Election not found with id: " + electionId));
        
        PositionModel positionModel = PositionModel.builder()
            .electionId(electionId)
            .positionName(createPositionDtoRequest.positionName())
            .build();
        
        
        JpaPositionEntity jpaPositionEntity = positionRepository.save(postitionMapper.toEntity(positionModel, jpaElectionEntity));
        return postitionMapper.toModel(jpaPositionEntity);
    }
}

package com.voting_system.election_service.repository;

import org.springframework.stereotype.Repository;

import com.voting_system.election_service.domain.ElectionPositionModel;
import com.voting_system.election_service.entity.JpaElectionEntity;
import com.voting_system.election_service.entity.JpaElectionPositionEntity;
import com.voting_system.election_service.entity.JpaPositionEntity;
import com.voting_system.election_service.exceptions.ElectionNotFoundException;
import com.voting_system.election_service.exceptions.PositionNotFoundException;
import com.voting_system.election_service.mappers.ElectionPositionMapper;
import com.voting_system.election_service.repository.interfaces.IElectionPositionRepository;
import com.voting_system.election_service.repository.jpa.JpaElectionPositionRepository;
import com.voting_system.election_service.repository.jpa.JpaElectionRepository;
import com.voting_system.election_service.repository.jpa.JpaPositionRepository;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class ElectionPositionRepositoryAdapter implements IElectionPositionRepository {
    private final JpaElectionPositionRepository jpaElectionPositionRepository;
    private final JpaPositionRepository jpaPositionRepository;
    private final JpaElectionRepository jpaElectionRepository;
    private final ElectionPositionMapper electionPositionMapper;

    @Override 
    public ElectionPositionModel save(ElectionPositionModel model) {
        JpaElectionEntity electionEntity = jpaElectionRepository.findById(model.getElectionId())
            .orElseThrow(() -> new ElectionNotFoundException("Election Not Found with ID:" + model.getElectionId()));

        JpaPositionEntity positionEntity = jpaPositionRepository.findById(model.getPositionId())
            .orElseThrow(() -> new PositionNotFoundException("Electoral Position not found with ID:" + model.getPositionId()));

            
        JpaElectionPositionEntity entity = jpaElectionPositionRepository.save(electionPositionMapper.toEntity(model, positionEntity, electionEntity));
        return electionPositionMapper.toModel(entity);
    }
}

package com.voting_system.election_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.domain.ElectionStatus;
import com.voting_system.election_service.entity.JpaElectionEntity;
import com.voting_system.election_service.exceptions.ElectionNotFoundException;
import com.voting_system.election_service.mappers.ElectionMapper;
import com.voting_system.election_service.repository.interfaces.IElectionRepository;
import com.voting_system.election_service.repository.jpa.JpaElectionRepository;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class ElectionRepositoryAdapter implements IElectionRepository {
    private final JpaElectionRepository jpaElectionRepository;
    private final ElectionMapper electionMapper;
    
    @Override 
    public ElectionModel save(ElectionModel model) {
        JpaElectionEntity entity = jpaElectionRepository.save(electionMapper.toEntity(model));
        return electionMapper.toModel(entity);
    }

    @Override
    public List<ElectionModel> getElections() {
        List<JpaElectionEntity> list = jpaElectionRepository.findAll();
        return list.stream()
            .map(electionMapper::toModel)
            .toList();
    }

    @Override
    public ElectionModel getElection(UUID electionId) {
        return jpaElectionRepository.findById(electionId)
            .map(electionMapper::toModel)
            .orElseThrow(() -> new ElectionNotFoundException("Election not found with ID: " + electionId));
    }

    @Override 
    public List<ElectionModel> findByStatusIn(List<ElectionStatus> statuses) {
        List<JpaElectionEntity> list = jpaElectionRepository.findAllByStatusIn(statuses);
        return list.stream()
            .map(electionMapper::toModel)
            .toList();
    }
}

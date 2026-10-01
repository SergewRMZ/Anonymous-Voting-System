package com.voting_system.election_service.repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.domain.CandidacyModel;
import com.voting_system.election_service.entity.JpaCandidacyEntity;
import com.voting_system.election_service.entity.JpaCandidateEntity;
import com.voting_system.election_service.entity.JpaElectionPositionEntity;
import com.voting_system.election_service.exceptions.CandidateNotFoundException;
import com.voting_system.election_service.exceptions.ElectionPositionNotFoundException;
import com.voting_system.election_service.mappers.CandidateMapper;
import com.voting_system.election_service.mappers.CandidacyMapper;
import com.voting_system.election_service.repository.interfaces.ICandidacyRepository;
import com.voting_system.election_service.repository.jpa.JpaCandidacyRepository;
import com.voting_system.election_service.repository.jpa.JpaCandidateRepository;
import com.voting_system.election_service.repository.jpa.JpaElectionPositionRepository;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class CandidacyRepositoryAdapter implements ICandidacyRepository {
    private final JpaCandidacyRepository jpaCandidacyRepository;
    private final JpaCandidateRepository jpaCandidateRepository;
    private final JpaElectionPositionRepository jpaElectionPositionRepository;
    private final CandidacyMapper candidacyMapper;
    private final CandidateMapper candidateMapper;
    
    @Override
    public CandidacyModel save(CandidacyModel model) {
        JpaCandidateEntity candidate = jpaCandidateRepository.findById(model.getCandidateId())
            .orElseThrow(() -> new CandidateNotFoundException("Candidate  not found with ID:" + model.getCandidateId()));
    
        JpaElectionPositionEntity electionPosition = jpaElectionPositionRepository.findById(model.getElectionPositionId())
            .orElseThrow(() -> new ElectionPositionNotFoundException("The election-position relation doesn't exists with ID" + model.getElectionPositionId()));
        
        JpaCandidacyEntity candidaciesEntity = jpaCandidacyRepository
            .save(candidacyMapper.toEntity(model, candidate, electionPosition));
        
        return candidacyMapper.toModel(candidaciesEntity);
    }

    @Override 
    public List<CandidacyModel> saveAll(UUID electionPositionId, List<UUID> candidateIds) {
        JpaElectionPositionEntity electionPosition = jpaElectionPositionRepository.findById(electionPositionId)
            .orElseThrow(() -> 
                new ElectionPositionNotFoundException(
                    "The election-position relation doesn't exists with ID" + electionPositionId
                )
            );
        
        List<JpaCandidateEntity> candidates = jpaCandidateRepository.findAllById(candidateIds);

        if(candidates.size() != candidateIds.size()) 
            throw new CandidateNotFoundException("One or more candidates were not found");

        List<JpaCandidacyEntity> candidacies = candidates.stream()
            .map(candidate -> candidacyMapper.toEntity(
                new CandidacyModel(null, candidate.getId(), electionPosition.getId(), Instant.now()), 
                candidate, 
                electionPosition)).toList();

        return jpaCandidacyRepository.saveAll(candidacies)
            .stream()
            .map(candidacyMapper::toModel)
            .toList();
    
    }
    
    @Override
    public List<CandidacyModel> getCandidacies() {
        return jpaCandidacyRepository.findAll()
            .stream()
            .map(candidacyMapper::toModel)
            .toList();
    }

    @Override
    public Map<UUID, List<CandidateModel>> getCandidatesByElectionPositionIds(List<UUID> electionPositionIds) {
        return jpaCandidacyRepository.findAllByElectionPositionEntity_IdIn(electionPositionIds)
            .stream()
            .collect(Collectors.groupingBy(
                candidacy -> candidacy.getElectionPositionEntity().getId(),
                Collectors.mapping(
                    candidacy -> candidateMapper.toModel(candidacy.getCandidateEntity()),
                    Collectors.toList()
                )
            ));
    }
}

package com.voting_system.election_service.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.entity.JpaCandidateEntity;
import com.voting_system.election_service.mappers.CandidateMapper;
import com.voting_system.election_service.repository.interfaces.ICandidateRepository;
import com.voting_system.election_service.repository.jpa.JpaCandidateRepository;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class CandidateRepositoryAdapter implements ICandidateRepository {
    private final JpaCandidateRepository jpaCandidateRepository;
    private final CandidateMapper candidateMapper;

    @Override
    public CandidateModel save(CandidateModel model) {
        JpaCandidateEntity entity = jpaCandidateRepository.save(candidateMapper.toEntity(model));
        return candidateMapper.toModel(entity);
    }

    @Override
    public List<CandidateModel> getCandidates() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCandidates'");
    }
    
}

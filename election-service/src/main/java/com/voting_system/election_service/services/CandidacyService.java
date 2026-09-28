package com.voting_system.election_service.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.voting_system.election_service.domain.CandidacyModel;
import com.voting_system.election_service.dto.CreateCandidacyDtoRequest;
import com.voting_system.election_service.repository.CandidacyRepositoryAdapter;
import com.voting_system.election_service.services.interfaces.ICandidacyService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CandidacyService implements ICandidacyService {
    private final CandidacyRepositoryAdapter candidacyRepositoryAdapter;

    @Override 
    public List<CandidacyModel> createCandidacies(UUID electionPositionId, CreateCandidacyDtoRequest request) {
        return candidacyRepositoryAdapter.saveAll(electionPositionId, request.candidateIds());
    }
}

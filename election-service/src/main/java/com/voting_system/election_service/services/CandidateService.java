package com.voting_system.election_service.services;
import org.springframework.stereotype.Service;

import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.dto.CreateCandidateDtoRequest;
import com.voting_system.election_service.repository.CandidateRepositoryAdapter;
import com.voting_system.election_service.services.interfaces.ICandidateService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CandidateService implements ICandidateService {
    private final CandidateRepositoryAdapter candidateRepositoryAdapter;

    @Override 
    public CandidateModel createCandidate(CreateCandidateDtoRequest request) {
        CandidateModel model = CandidateModel.builder()
            .name(request.name())
            .lastName(request.lastName())
            .description(request.description())
            .isActive(true)
            .build();

        return candidateRepositoryAdapter.save(model);
    }
}

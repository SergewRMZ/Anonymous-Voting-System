package com.voting_system.election_service.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.voting_system.election_service.domain.ElectionPositionModel;
import com.voting_system.election_service.repository.ElectionPositionRepositoryAdapter;
import com.voting_system.election_service.services.interfaces.IElectionPositionService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ElectionPositionService implements IElectionPositionService {
    private final ElectionPositionRepositoryAdapter electionPositionRepositoryAdapter;

    @Override 
    public ElectionPositionModel associateElectoralPosition(UUID electionId, UUID positionId) {
        ElectionPositionModel model = ElectionPositionModel.builder()
            .electionId(electionId)
            .positionId(positionId)
            .build();
        
        return electionPositionRepositoryAdapter.save(model);
    }
}

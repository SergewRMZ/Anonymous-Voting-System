package com.voting_system.election_service.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.dto.CreatePositionDtoRequest;
import com.voting_system.election_service.repository.PositionRepositoryAdapter;
import com.voting_system.election_service.services.interfaces.IPositionService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PositionService implements IPositionService {
    private final PositionRepositoryAdapter positionRepository;
    @Override 
    public PositionModel createPosition(CreatePositionDtoRequest createPositionDtoRequest) {
        PositionModel positionModel = PositionModel.builder()
            .positionName(createPositionDtoRequest.positionName())
            .build();

        return positionRepository.save(positionModel);
    }

    @Override 
    public List<PositionModel> getPositions() {
        return positionRepository.getPositions();
    }
}

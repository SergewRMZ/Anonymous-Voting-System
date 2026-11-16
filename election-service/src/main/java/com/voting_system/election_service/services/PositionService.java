package com.voting_system.election_service.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.dto.CreatePositionDtoRequest;
import com.voting_system.election_service.exceptions.PositionAlreadyExistsException;
import com.voting_system.election_service.repository.PositionRepositoryAdapter;
import com.voting_system.election_service.services.interfaces.IPositionService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PositionService implements IPositionService {
    private final PositionRepositoryAdapter positionRepository;
    @Override 
    public PositionModel createPosition(CreatePositionDtoRequest createPositionDtoRequest) {
        String positionName = createPositionDtoRequest.positionName().trim();
        if(positionRepository.existsByPositionName(positionName)) {
            throw new PositionAlreadyExistsException(positionName);
        }

        PositionModel positionModel = PositionModel.builder()
            .positionName(positionName)
            .build();

        // La restricción única de la base resuelve el caso de dos solicitudes simultáneas.
        try {
            return positionRepository.save(positionModel);
        } catch (DataIntegrityViolationException ex) {
            throw new PositionAlreadyExistsException(positionName);
        }
    }

    @Override 
    public List<PositionModel> getPositions() {
        return positionRepository.getPositions();
    }
}

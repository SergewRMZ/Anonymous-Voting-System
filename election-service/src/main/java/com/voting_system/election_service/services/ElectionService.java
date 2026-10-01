package com.voting_system.election_service.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.domain.ElectionPositionModel;
import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.dto.CreateElectionDtoRequest;
import com.voting_system.election_service.dto.ElectionCandidateDtoResponse;
import com.voting_system.election_service.dto.ElectionDetailsDtoResponse;
import com.voting_system.election_service.dto.ElectionPositionDetailsDtoResponse;
import com.voting_system.election_service.exceptions.PositionNotFoundException;
import com.voting_system.election_service.repository.interfaces.ICandidacyRepository;
import com.voting_system.election_service.repository.interfaces.IElectionPositionRepository;
import com.voting_system.election_service.repository.interfaces.IElectionRepository;
import com.voting_system.election_service.repository.interfaces.IPositionRepository;
import com.voting_system.election_service.services.interfaces.IElectionService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ElectionService implements IElectionService {
    private final IElectionRepository electionRepository;
    private final IElectionPositionRepository electionPositionRepository;
    private final IPositionRepository positionRepository;
    private final ICandidacyRepository candidacyRepository;

    @Override 
    public ElectionModel createElection(CreateElectionDtoRequest createElectionDtoRequest) {
        ElectionModel electionModel = ElectionModel.builder()
            .name(createElectionDtoRequest.name())
            .description(createElectionDtoRequest.description())
            .startDate(createElectionDtoRequest.startDate())
            .endDate(createElectionDtoRequest.endDate()).build();

        
        electionModel.createElection();
        return electionRepository.save(electionModel);
    }

    @Override 
    public List<ElectionModel> getElections() {
        return electionRepository.getElections();
    }

    @Override
    @Transactional(readOnly = true)
    public ElectionDetailsDtoResponse getElectionDetails(UUID electionId) {
        ElectionModel election = electionRepository.getElection(electionId);
        List<ElectionPositionModel> electionPositions = electionPositionRepository.getByElectionId(electionId);

        List<UUID> positionIds = electionPositions.stream()
            .map(ElectionPositionModel::getPositionId)
            .toList();
        Map<UUID, PositionModel> positionsById = positionIds.isEmpty()
            ? Map.of()
            : positionRepository.getByIds(positionIds).stream()
                .collect(Collectors.toMap(PositionModel::getId, Function.identity()));

        List<UUID> electionPositionIds = electionPositions.stream()
            .map(ElectionPositionModel::getId)
            .toList();
        Map<UUID, List<CandidateModel>> candidatesByElectionPositionId = electionPositionIds.isEmpty()
            ? Map.of()
            : candidacyRepository.getCandidatesByElectionPositionIds(electionPositionIds);

        List<ElectionPositionDetailsDtoResponse> positionResponses = electionPositions.stream()
            .map(electionPosition -> {
                PositionModel position = positionsById.get(electionPosition.getPositionId());
                if (position == null) {
                    throw new PositionNotFoundException(
                        "Electoral position not found with ID: " + electionPosition.getPositionId()
                    );
                }
                List<ElectionCandidateDtoResponse> candidateResponses = candidatesByElectionPositionId
                    .getOrDefault(electionPosition.getId(), List.of())
                    .stream()
                    .map(ElectionCandidateDtoResponse::fromModel)
                    .toList();

                return new ElectionPositionDetailsDtoResponse(
                    electionPosition.getId(),
                    position.getId(),
                    position.getPositionName(),
                    candidateResponses
                );
            })
            .toList();

        return new ElectionDetailsDtoResponse(
            election.getId(),
            election.getName(),
            election.getDescription(),
            election.getStartDate(),
            election.getEndDate(),
            election.getStatus(),
            positionResponses
        );
    }
}

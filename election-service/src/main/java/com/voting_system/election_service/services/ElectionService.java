package com.voting_system.election_service.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voting_system.election_service.domain.CandidacyModel;
import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.domain.ElectionPositionModel;
import com.voting_system.election_service.domain.ElectionStatus;
import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.dto.CreateElectionDtoRequest;
import com.voting_system.election_service.dto.ElectionCandidateDtoResponse;
import com.voting_system.election_service.dto.ElectionDetailsDtoResponse;
import com.voting_system.election_service.dto.ElectionPositionDetailsDtoResponse;
import com.voting_system.election_service.exceptions.CandidateNotFoundException;
import com.voting_system.election_service.exceptions.ElectionNotFoundException;
import com.voting_system.election_service.exceptions.PositionNotFoundException;
import com.voting_system.election_service.repository.interfaces.ICandidacyRepository;
import com.voting_system.election_service.repository.interfaces.ICandidateRepository;
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
    private final ICandidateRepository candidateRepository;
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
    public ElectionModel publishElection(UUID electionId) {
        ElectionModel electionModel = electionRepository.getElection(electionId);
        electionModel.publishElection();
        return electionRepository.save(electionModel);
    }


    @Override 
    public ElectionModel validateElection(UUID electionId) {
        ElectionModel electionModel = electionRepository.getElection(electionId);
        electionModel.validateElection();
        return electionRepository.save(electionModel);
    }


    @Override 
    public ElectionModel activateElection(UUID electionId) {
        ElectionModel electionModel = electionRepository.getElection(electionId);
        electionModel.activateElection();
        return electionRepository.save(electionModel);
    }


    @Override 
    public List<ElectionModel> getElections(List<String> roles) {
        List<ElectionStatus> allowedStatuses = getAllowedStatusesForRoles(roles);
        return electionRepository.findByStatusIn(allowedStatuses);
    }

    @Override
    @Transactional(readOnly = true)
    public ElectionDetailsDtoResponse getElectionDetails(UUID electionId, List<String> roles) {
        ElectionModel election = electionRepository.getElection(electionId);
        List<ElectionStatus> allowedStatuses = getAllowedStatusesForRoles(roles);

        // Check if the election's status is allowed for the user's roles.
        if (!allowedStatuses.contains(election.getStatus())) {
            throw new ElectionNotFoundException(
                "This user doesn't have permission to view election with ID: " + electionId
            );
        }

        List<ElectionPositionModel> electionPositions = electionPositionRepository.getByElectionId(electionId);

        /**
         * Obtener todos los identificadores de las entidades de tipo Position
         * a partir del identificador que relaciona una elección con una posición
         * electoral.
         */
        List<UUID> positionIds = electionPositions.stream()
            .map(ElectionPositionModel::getPositionId)
            .toList();

        /**
         * Se utilizan los identificadores de las entidades de tipo Position
         * para poder realizar una query respecto a sus modelos. Se genera un
         * mapa que asocia las el modelo con el id del modelo de Position.
         */
        Map<UUID, PositionModel> positionsById = positionIds.isEmpty()
            ? Map.of()
            : positionRepository.getByIds(positionIds).stream()
                .collect(Collectors.toMap(PositionModel::getId, Function.identity()));

        /**
         * Se obtienen los identificadores de las entidades
         * entre una posición electoral y una elección del sistema.
         */
        List<UUID> electionPositionIds = electionPositions.stream()
            .map(ElectionPositionModel::getId)
            .toList();

        /**
         * Con base en los identificadores de la tabla que relaciona una posición electoral
         * con una elección. Se obtienen los modelos de dominio de la entidad Candidacy.
         */
        List<CandidacyModel> candidacies = 
            candidacyRepository.getCandidaciesByElectionPositionIds(electionPositionIds);

        /**
         * Con base en el modelo de dominio que representa a una candidatura del sistema
         * se obtienen los identificadores de los candidatos.
         */
        List<UUID> candidatesIds = candidacies.stream()
            .map(CandidacyModel::getCandidateId)
            .distinct()
            .toList();

        List<CandidateModel> candidates = candidatesIds.isEmpty()
            ? List.of()
            : candidateRepository.findAllByIds(candidatesIds);
        
        Map<UUID, List<CandidacyModel>> candidaciesByElectionPositionId =
            candidacies.stream()
                .collect(Collectors.groupingBy(
                    CandidacyModel::getElectionPositionId
                ));

        Map<UUID, CandidateModel> candidatesById = candidates
            .stream()
            .collect(Collectors.toMap(
                CandidateModel::getId,
                Function.identity()
            ));


        List<ElectionPositionDetailsDtoResponse> positionResponses = electionPositions
            .stream()
            .map(electionPosition -> {

                PositionModel position = 
                    positionsById.get(electionPosition.getPositionId());

                if (position == null) {
                    throw new PositionNotFoundException(
                        "Electoral position not found with ID: " + electionPosition.getPositionId()
                    );
                }

                List<ElectionCandidateDtoResponse> candidateResponses = 
                    candidaciesByElectionPositionId
                        .getOrDefault(electionPosition.getId(), List.of())
                        .stream()
                        .map(candidacy -> {
                            CandidateModel candidateModel =
                                candidatesById.get(candidacy.getCandidateId());

                            if (candidateModel == null) {
                                throw new CandidateNotFoundException(
                                    "Candidate not found with ID: "
                                    + candidacy.getCandidateId()
                                );
                            }

                            return ElectionCandidateDtoResponse.fromModels(
                                candidacy, 
                                candidateModel
                            );
                        })
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

    private List<ElectionStatus> getAllowedStatusesForRoles(List<String> roles) {
        if(roles.contains("ROLE_ADMIN")) {
            return List.of(
                ElectionStatus.DRAFT,
                ElectionStatus.PUBLISHED,
                ElectionStatus.VERIFIED,
                ElectionStatus.ACTIVE,
                ElectionStatus.CLOSED,
                ElectionStatus.FINISHED
            );
        }

        else if(roles.contains("ROLE_AUTHORITY")) {
            return List.of(
                ElectionStatus.PUBLISHED,
                ElectionStatus.VERIFIED,
                ElectionStatus.ACTIVE,
                ElectionStatus.CLOSED,
                ElectionStatus.FINISHED
            );
        }

        else {
            return List.of(
                ElectionStatus.ACTIVE,
                ElectionStatus.CLOSED,
                ElectionStatus.FINISHED
            );
        }
    }
}

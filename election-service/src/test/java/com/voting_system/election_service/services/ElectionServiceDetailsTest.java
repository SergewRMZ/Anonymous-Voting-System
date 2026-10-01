package com.voting_system.election_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.domain.ElectionPositionModel;
import com.voting_system.election_service.domain.ElectionStatus;
import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.dto.ElectionDetailsDtoResponse;
import com.voting_system.election_service.repository.interfaces.ICandidacyRepository;
import com.voting_system.election_service.repository.interfaces.IElectionPositionRepository;
import com.voting_system.election_service.repository.interfaces.IElectionRepository;
import com.voting_system.election_service.repository.interfaces.IPositionRepository;

class ElectionServiceDetailsTest {
    private final IElectionRepository electionRepository = mock(IElectionRepository.class);
    private final IElectionPositionRepository electionPositionRepository = mock(IElectionPositionRepository.class);
    private final IPositionRepository positionRepository = mock(IPositionRepository.class);
    private final ICandidacyRepository candidacyRepository = mock(ICandidacyRepository.class);
    private final ElectionService electionService = new ElectionService(
        electionRepository,
        electionPositionRepository,
        positionRepository,
        candidacyRepository
    );

    @Test
    void returnsElectionWithPositionsAndTheirCandidates() {
        UUID electionId = UUID.randomUUID();
        UUID electionPositionId = UUID.randomUUID();
        UUID positionId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        ElectionModel election = ElectionModel.builder()
            .id(electionId)
            .name("Student Council")
            .description("Annual election")
            .startDate(createdAt)
            .endDate(createdAt.plusSeconds(3600))
            .status(ElectionStatus.DRAFT)
            .build();
        ElectionPositionModel electionPosition = ElectionPositionModel.builder()
            .id(electionPositionId)
            .electionId(electionId)
            .positionId(positionId)
            .build();
        CandidateModel candidate = CandidateModel.builder()
            .id(candidateId)
            .name("Ada")
            .lastName("Lovelace")
            .description("Candidate")
            .createdAt(createdAt)
            .build();

        when(electionRepository.getElection(electionId)).thenReturn(election);
        when(electionPositionRepository.getByElectionId(electionId)).thenReturn(List.of(electionPosition));
        when(positionRepository.getByIds(List.of(positionId))).thenReturn(
            List.of(PositionModel.builder().id(positionId).positionName("President").build())
        );
        when(candidacyRepository.getCandidatesByElectionPositionIds(List.of(electionPositionId)))
            .thenReturn(Map.of(electionPositionId, List.of(candidate)));

        ElectionDetailsDtoResponse response = electionService.getElectionDetails(electionId);

        assertEquals(electionId, response.electionId());
        assertEquals("Annual election", response.description());
        assertEquals(1, response.positions().size());
        assertEquals(electionPositionId, response.positions().get(0).electionPositionId());
        assertEquals("President", response.positions().get(0).positionName());
        assertEquals(candidateId, response.positions().get(0).candidates().get(0).candidateId());
        assertEquals("Ada", response.positions().get(0).candidates().get(0).name());
    }

    @Test
    void returnsEmptyPositionsWhenElectionHasNoAssociatedPositions() {
        UUID electionId = UUID.randomUUID();
        ElectionModel election = ElectionModel.builder()
            .id(electionId)
            .name("Student Council")
            .build();
        when(electionRepository.getElection(electionId)).thenReturn(election);
        when(electionPositionRepository.getByElectionId(electionId)).thenReturn(List.of());

        ElectionDetailsDtoResponse response = electionService.getElectionDetails(electionId);

        assertEquals(List.of(), response.positions());
        verify(positionRepository, org.mockito.Mockito.never()).getByIds(org.mockito.ArgumentMatchers.anyList());
        verify(candidacyRepository, org.mockito.Mockito.never())
            .getCandidatesByElectionPositionIds(org.mockito.ArgumentMatchers.anyList());
    }
}

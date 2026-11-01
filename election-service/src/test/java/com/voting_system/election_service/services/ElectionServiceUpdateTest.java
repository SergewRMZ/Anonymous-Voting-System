package com.voting_system.election_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.domain.ElectionStatus;
import com.voting_system.election_service.dto.UpdateElectionDtoRequest;
import com.voting_system.election_service.exceptions.ElectionNotFoundException;
import com.voting_system.election_service.exceptions.InvalidElectionStateException;
import com.voting_system.election_service.repository.interfaces.ICandidacyRepository;
import com.voting_system.election_service.repository.interfaces.ICandidateRepository;
import com.voting_system.election_service.repository.interfaces.IElectionPositionRepository;
import com.voting_system.election_service.repository.interfaces.IElectionRepository;
import com.voting_system.election_service.repository.interfaces.IPositionRepository;

class ElectionServiceUpdateTest {
    private IElectionRepository electionRepository;
    private ElectionService electionService;

    private final UUID electionId = UUID.randomUUID();
    private final Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
    private final Instant end = start.plus(2, ChronoUnit.HOURS);

    @BeforeEach
    void setUp() {
        electionRepository = mock(IElectionRepository.class);
        electionService = new ElectionService(
            electionRepository,
            mock(IElectionPositionRepository.class),
            mock(ICandidateRepository.class),
            mock(IPositionRepository.class),
            mock(ICandidacyRepository.class)
        );
    }

    private ElectionModel election(ElectionStatus status) {
        return ElectionModel.builder()
            .id(electionId)
            .name("Original")
            .description("Descripción")
            .startDate(start)
            .endDate(end)
            .status(status)
            .build();
    }

    @Test
    void updatesAndPersistsDraftElection() {
        when(electionRepository.getElection(electionId)).thenReturn(election(ElectionStatus.DRAFT));
        when(electionRepository.save(any(ElectionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ElectionModel updated = electionService.updateElection(
            electionId,
            new UpdateElectionDtoRequest("Nueva", "Otra descripción", start, end.plus(1, ChronoUnit.HOURS))
        );

        assertEquals("Nueva", updated.getName());
        assertEquals(ElectionStatus.DRAFT, updated.getStatus());
        verify(electionRepository).save(updated);
    }

    @Test
    void doesNotPersistWhenElectionIsPublished() {
        when(electionRepository.getElection(electionId)).thenReturn(election(ElectionStatus.PUBLISHED));

        assertThrows(InvalidElectionStateException.class, () -> electionService.updateElection(
            electionId, new UpdateElectionDtoRequest("Nueva", "Desc", start, end)));

        verify(electionRepository, never()).save(any());
    }

    @Test
    void propagatesNotFound() {
        when(electionRepository.getElection(electionId)).thenThrow(new ElectionNotFoundException(electionId));

        assertThrows(ElectionNotFoundException.class, () -> electionService.updateElection(
            electionId, new UpdateElectionDtoRequest("Nueva", "Desc", start, end)));
    }
}

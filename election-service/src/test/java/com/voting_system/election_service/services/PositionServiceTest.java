package com.voting_system.election_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.dto.CreatePositionDtoRequest;
import com.voting_system.election_service.exceptions.PositionAlreadyExistsException;
import com.voting_system.election_service.repository.PositionRepositoryAdapter;

class PositionServiceTest {
    private PositionRepositoryAdapter positionRepository;
    private PositionService positionService;

    @BeforeEach
    void setUp() {
        positionRepository = mock(PositionRepositoryAdapter.class);
        positionService = new PositionService(positionRepository);
    }

    @Test
    void creaElCargoConElNombreSinEspaciosAlrededor() {
        when(positionRepository.existsByPositionName("Presidente")).thenReturn(false);
        when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PositionModel created = positionService.createPosition(new CreatePositionDtoRequest("  Presidente  "));

        assertEquals("Presidente", created.getPositionName());
    }

    @Test
    void rechazaUnCargoQueYaExiste() {
        when(positionRepository.existsByPositionName("Presidente")).thenReturn(true);

        assertThrows(
            PositionAlreadyExistsException.class,
            () -> positionService.createPosition(new CreatePositionDtoRequest("Presidente"))
        );
        verify(positionRepository, never()).save(any());
    }

    @Test
    void unaViolacionDeLaRestriccionUnicaSeReportaComoCargoDuplicado() {
        // Dos solicitudes simultáneas: ambas pasan la revisión y la base rechaza la segunda.
        when(positionRepository.existsByPositionName("Presidente")).thenReturn(false);
        when(positionRepository.save(any())).thenThrow(new DataIntegrityViolationException("unique_position_name"));

        assertThrows(
            PositionAlreadyExistsException.class,
            () -> positionService.createPosition(new CreatePositionDtoRequest("Presidente"))
        );
    }
}

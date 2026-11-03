package com.voting_system.tally_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.voting_system.tally_service.client.BulletinBoardServiceClient;
import com.voting_system.tally_service.client.ElectionServiceClient;
import com.voting_system.tally_service.client.dto.ElectionStatus;
import com.voting_system.tally_service.client.dto.GetElectionStatusDto;
import com.voting_system.tally_service.client.dto.GetElectionStructureDto;
import com.voting_system.tally_service.client.dto.PageResponseDto;
import com.voting_system.tally_service.client.dto.VoteBallotDto;
import com.voting_system.tally_service.crypto.ElGamalAcumulator;
import com.voting_system.tally_service.crypto.ElGamalDecryptor;
import com.voting_system.tally_service.exception.ElectionAlreadyTalliedException;
import com.voting_system.tally_service.exception.ElectionNotClosedException;
import com.voting_system.tally_service.model.ElectionResultModel;
import com.voting_system.tally_service.repository.ElectionResultRepositoryPort;
import com.voting_system.tally_service.repository.PrivateEncryptionKeyRepository;

class TallyServiceTest {
    private BulletinBoardServiceClient bulletinBoardServiceClient;
    private ElectionServiceClient electionServiceClient;
    private ElGamalDecryptor elGamalDecryptor;
    private PrivateEncryptionKeyRepository privateEncryptionKeyRepository;
    private ElectionResultRepositoryPort electionResultRepository;
    private TallyService tallyService;

    private final UUID electionId = UUID.randomUUID();
    private final UUID positionId = UUID.randomUUID();
    private final UUID candidacyId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        bulletinBoardServiceClient = mock(BulletinBoardServiceClient.class);
        electionServiceClient = mock(ElectionServiceClient.class);
        elGamalDecryptor = mock(ElGamalDecryptor.class);
        privateEncryptionKeyRepository = mock(PrivateEncryptionKeyRepository.class);
        electionResultRepository = mock(ElectionResultRepositoryPort.class);
        tallyService = new TallyService(
            bulletinBoardServiceClient,
            electionServiceClient,
            mock(ElGamalAcumulator.class),
            elGamalDecryptor,
            privateEncryptionKeyRepository,
            electionResultRepository
        );
    }

    private void givenElectionStatus(ElectionStatus status) {
        GetElectionStatusDto dto = mock(GetElectionStatusDto.class);
        when(dto.status()).thenReturn(status);
        when(electionServiceClient.getElectionStatus(electionId)).thenReturn(dto);
    }

    @SuppressWarnings("unchecked")
    private void givenNoPreviousResultsAndOneCandidacy() {
        when(electionResultRepository.existsByElectionId(electionId)).thenReturn(false);
        when(electionServiceClient.getElectionStructureDto(electionId)).thenReturn(
            new GetElectionStructureDto(electionId, List.of(
                new GetElectionStructureDto.ElectionPositionDto(positionId, List.of(candidacyId))
            ))
        );
        PageResponseDto<VoteBallotDto> emptyPage = mock(PageResponseDto.class);
        when(emptyPage.content()).thenReturn(List.of());
        when(emptyPage.last()).thenReturn(true);
        when(bulletinBoardServiceClient.getBallots(electionId, positionId, candidacyId, 0, 100)).thenReturn(emptyPage);
        when(privateEncryptionKeyRepository.readPrivateKey(electionId)).thenReturn(new byte[] { 1 });
        when(elGamalDecryptor.decrypt(any(), any(), any(), anyInt())).thenReturn(0);
        when(electionResultRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void primerConteoGuardaLosResultadosAntesDeFinalizar() {
        givenElectionStatus(ElectionStatus.CLOSED);
        givenNoPreviousResultsAndOneCandidacy();

        List<ElectionResultModel> results = tallyService.voteTallying(electionId);

        assertEquals(1, results.size());
        assertEquals(candidacyId, results.get(0).getCandidacyId());
        InOrder order = inOrder(electionResultRepository, electionServiceClient);
        order.verify(electionResultRepository).saveAll(anyList());
        order.verify(electionServiceClient).finishElection(electionId);
    }

    @Test
    void reintentoReutilizaLosResultadosGuardadosYFinalizaLaEleccion() {
        givenElectionStatus(ElectionStatus.CLOSED);
        List<ElectionResultModel> saved = List.of(ElectionResultModel.builder().candidacyId(candidacyId).result(7).build());
        when(electionResultRepository.existsByElectionId(electionId)).thenReturn(true);
        when(electionResultRepository.findAllByElectionId(electionId)).thenReturn(saved);

        List<ElectionResultModel> results = tallyService.voteTallying(electionId);

        assertEquals(saved, results);
        verify(electionServiceClient).finishElection(electionId);
        verify(electionResultRepository, never()).saveAll(anyList());
        verify(privateEncryptionKeyRepository, never()).readPrivateKey(any());
        verify(electionServiceClient, never()).getElectionStructureDto(any());
    }

    @Test
    void fallaAlFinalizarConservaLosResultadosParaElReintento() {
        givenElectionStatus(ElectionStatus.CLOSED);
        givenNoPreviousResultsAndOneCandidacy();
        doThrow(new IllegalStateException("election-service no disponible"))
            .when(electionServiceClient).finishElection(electionId);

        assertThrows(IllegalStateException.class, () -> tallyService.voteTallying(electionId));

        verify(electionResultRepository).saveAll(anyList());
    }

    @Test
    void eleccionFinalizadaSeRechazaSinVolverAContar() {
        givenElectionStatus(ElectionStatus.FINISHED);

        assertThrows(ElectionAlreadyTalliedException.class, () -> tallyService.voteTallying(electionId));

        verify(electionResultRepository, never()).saveAll(anyList());
        verify(electionServiceClient, never()).finishElection(any());
    }

    @Test
    void eleccionNoCerradaSeRechazaSinReutilizarResultados() {
        givenElectionStatus(ElectionStatus.ACTIVE);

        assertThrows(ElectionNotClosedException.class, () -> tallyService.voteTallying(electionId));

        verify(electionResultRepository, never()).existsByElectionId(any());
        verify(electionServiceClient, never()).finishElection(any());
    }
}

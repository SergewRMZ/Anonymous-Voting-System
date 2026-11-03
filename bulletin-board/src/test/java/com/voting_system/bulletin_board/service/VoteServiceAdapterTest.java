package com.voting_system.bulletin_board.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.voting_system.bulletin_board.client.AuthorizationServiceClient;
import com.voting_system.bulletin_board.client.ElectionServiceClient;
import com.voting_system.bulletin_board.client.ElectionStatus;
import com.voting_system.bulletin_board.client.GetElectionStatusDto;
import com.voting_system.bulletin_board.crypto.AuthorizationPublicKeyCache;
import com.voting_system.bulletin_board.crypto.CryptoUtils;
import com.voting_system.bulletin_board.crypto.RsaBssaVerifier;
import com.voting_system.bulletin_board.dto.CreateVoteRequestDto;
import com.voting_system.bulletin_board.exceptions.DuplicateVoteException;
import com.voting_system.bulletin_board.exceptions.ElectionNotActiveException;
import com.voting_system.bulletin_board.model.Ballot;
import com.voting_system.bulletin_board.model.ElGamalCiphertext;
import com.voting_system.bulletin_board.model.EncryptedVote;
import com.voting_system.bulletin_board.model.Position;
import com.voting_system.bulletin_board.model.VoteModel;
import com.voting_system.bulletin_board.repository.interfaces.VoteRepositoryPort;
import com.voting_system.bulletin_board.utils.JsonUtils;

import tools.jackson.databind.ObjectMapper;

class VoteServiceAdapterTest {
    private VoteRepositoryPort voteRepository;
    private ElectionServiceClient electionServiceClient;
    private RsaBssaVerifier verifier;
    private VoteServiceAdapter voteService;

    private final UUID electionId = UUID.randomUUID();
    private final UUID authorizationKeysId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        voteRepository = mock(VoteRepositoryPort.class);
        electionServiceClient = mock(ElectionServiceClient.class);
        verifier = mock(RsaBssaVerifier.class);

        AuthorizationPublicKeyCache cache = new AuthorizationPublicKeyCache();
        cache.put(authorizationKeysId, mock(PublicKey.class));

        voteService = new VoteServiceAdapter(
            voteRepository,
            mock(AuthorizationServiceClient.class),
            electionServiceClient,
            cache,
            verifier
        );
    }

    private void electionIs(ElectionStatus status) {
        when(electionServiceClient.getElectionStatus(electionId))
            .thenReturn(new GetElectionStatusDto(electionId, status));
    }

    private CreateVoteRequestDto vote() {
        EncryptedVote encryptedVote = new EncryptedVote(
            authorizationKeysId,
            List.of(new Position(
                UUID.randomUUID(),
                List.of(new Ballot(UUID.randomUUID(), new ElGamalCiphertext("c1", "c2")))
            ))
        );
        String json = JsonUtils.canonicalize(new ObjectMapper().writeValueAsString(encryptedVote));
        String digest = Base64.getEncoder().encodeToString(CryptoUtils.sha3_384(json));
        return new CreateVoteRequestDto(encryptedVote, "AAAA", digest, "AAAA");
    }

    @ParameterizedTest
    @EnumSource (value = ElectionStatus.class, names = "ACTIVE", mode = EnumSource.Mode.EXCLUDE)
    void rejectsVoteWhenElectionIsNotActive(ElectionStatus status) {
        electionIs(status);

        assertThrows(
            ElectionNotActiveException.class,
            () -> voteService.submitVote(electionId, vote())
        );
        verify(voteRepository, never()).save(any());
    }

    @Test
    void savesVoteWhenElectionIsActive() {
        UUID voteId = UUID.randomUUID();
        electionIs(ElectionStatus.ACTIVE);
        when(verifier.verify(any(), any(), any())).thenReturn(true);
        when(voteRepository.save(any())).thenReturn(VoteModel.builder().id(voteId).build());

        assertEquals(voteId, voteService.submitVote(electionId, vote()).voteId());
    }

    @Test
    void duplicateVoteIsReportedEvenIfElectionAlreadyClosed() {
        when(voteRepository.existsByElectionIdAndSignature(electionId, "AAAA")).thenReturn(true);

        assertThrows(
            DuplicateVoteException.class,
            () -> voteService.submitVote(electionId, vote())
        );
        verify(electionServiceClient, never()).getElectionStatus(any());
    }
}

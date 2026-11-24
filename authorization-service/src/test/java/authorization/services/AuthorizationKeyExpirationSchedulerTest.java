package authorization.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import authorization.client.ElectionServiceClient;
import authorization.client.ElectionStatus;
import authorization.domain.exception.ElectionStateUnavailableException;
import authorization.domain.model.BlindSignaturePublicKeyModel;
import authorization.domain.model.KeyStatus;
import authorization.services.interfaces.IBlindSignaturePublicKeyService;

class AuthorizationKeyExpirationSchedulerTest {
    private IBlindSignaturePublicKeyService keyService;
    private ElectionServiceClient electionServiceClient;
    private AuthorizationKeyExpirationScheduler scheduler;

    @BeforeEach
    void setUp() {
        keyService = mock(IBlindSignaturePublicKeyService.class);
        electionServiceClient = mock(ElectionServiceClient.class);
        scheduler = new AuthorizationKeyExpirationScheduler(keyService, electionServiceClient);
    }

    private BlindSignaturePublicKeyModel activeKey(UUID electionId) {
        return BlindSignaturePublicKeyModel.builder()
            .id(UUID.randomUUID())
            .electionId(electionId)
            .status(KeyStatus.ACTIVE)
            .build();
    }

    @Test
    void expiraLasClavesDeEleccionesCerradasOFinalizadas() {
        UUID closed = UUID.randomUUID();
        UUID finished = UUID.randomUUID();
        when(keyService.getActiveKeys()).thenReturn(List.of(activeKey(closed), activeKey(finished)));
        when(electionServiceClient.getElectionStatus(closed)).thenReturn(ElectionStatus.CLOSED);
        when(electionServiceClient.getElectionStatus(finished)).thenReturn(ElectionStatus.FINISHED);

        scheduler.expireKeysOfClosedElections();

        verify(keyService).expireActiveElectionKeys(closed);
        verify(keyService).expireActiveElectionKeys(finished);
    }

    @Test
    void noExpiraLaClaveDeUnaEleccionActiva() {
        UUID active = UUID.randomUUID();
        when(keyService.getActiveKeys()).thenReturn(List.of(activeKey(active)));
        when(electionServiceClient.getElectionStatus(active)).thenReturn(ElectionStatus.ACTIVE);

        scheduler.expireKeysOfClosedElections();

        verify(keyService, never()).expireActiveElectionKeys(active);
    }

    @Test
    void unaEleccionQueFallaNoDetieneLaRevisionDeLasDemas() {
        UUID unavailable = UUID.randomUUID();
        UUID closed = UUID.randomUUID();
        when(keyService.getActiveKeys()).thenReturn(List.of(activeKey(unavailable), activeKey(closed)));
        when(electionServiceClient.getElectionStatus(unavailable))
            .thenThrow(new ElectionStateUnavailableException(unavailable));
        when(electionServiceClient.getElectionStatus(closed)).thenReturn(ElectionStatus.CLOSED);

        scheduler.expireKeysOfClosedElections();

        verify(keyService, never()).expireActiveElectionKeys(unavailable);
        verify(keyService).expireActiveElectionKeys(closed);
    }
}

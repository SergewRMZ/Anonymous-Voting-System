package authorization.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import authorization.client.ElectionServiceClient;
import authorization.client.ElectionStatus;
import authorization.domain.exception.ElectionNotActiveException;
import authorization.dto.BlindSignatureRequest;
import authorization.repository.interfaces.BlindSignatureRepositoryPort;
import authorization.services.cripto.RsaBssaService;
import authorization.services.interfaces.IBlindSignaturePublicKeyService;

class BlindSignatureServiceTest {
    private IBlindSignaturePublicKeyService keyService;
    private RsaBssaService rsaBssaService;
    private ElectionServiceClient electionServiceClient;
    private BlindSignatureService service;

    @BeforeEach
    void setUp() {
        keyService = mock(IBlindSignaturePublicKeyService.class);
        rsaBssaService = mock(RsaBssaService.class);
        electionServiceClient = mock(ElectionServiceClient.class);
        service = new BlindSignatureService(
            mock(BlindSignatureRepositoryPort.class), keyService, rsaBssaService, electionServiceClient);
    }

    @ParameterizedTest
    @EnumSource(value = ElectionStatus.class, names = "ACTIVE", mode = EnumSource.Mode.EXCLUDE)
    void noFirmaSiLaEleccionNoEstaActiva(ElectionStatus status) {
        UUID electionId = UUID.randomUUID();
        when(electionServiceClient.getElectionStatus(electionId)).thenReturn(status);

        assertThrows(ElectionNotActiveException.class, () ->
            service.generateBlindSignature(electionId, UUID.randomUUID(), new BlindSignatureRequest("AQID")));

        verify(keyService, never()).getActivePublicKeyByElectionId(any());
        verify(rsaBssaService, never()).sign(any(), any());
    }
}

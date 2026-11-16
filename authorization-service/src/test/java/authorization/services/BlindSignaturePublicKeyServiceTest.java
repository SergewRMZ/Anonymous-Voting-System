package authorization.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.dao.DataIntegrityViolationException;

import authorization.domain.exception.CreatedKeyAlreadyExistsException;
import authorization.domain.model.BlindSignaturePublicKeyModel;
import authorization.mappers.RsaKeyMapper;
import authorization.repository.interfaces.BlindSignaturePrivateKeyRepositoryPort;
import authorization.repository.interfaces.BlindSignaturePublicKeyRepositoryPort;
import authorization.services.cripto.RsaBssaService;

class BlindSignaturePublicKeyServiceTest {
    private BlindSignaturePublicKeyRepositoryPort publicKeyRepository;
    private BlindSignaturePrivateKeyRepositoryPort privateKeyRepository;
    private RsaBssaService rsaBssaService;
    private BlindSignaturePublicKeyService service;

    private final UUID electionId = UUID.randomUUID();

    @BeforeEach
    void setUp() throws Exception {
        publicKeyRepository = mock(BlindSignaturePublicKeyRepositoryPort.class);
        privateKeyRepository = mock(BlindSignaturePrivateKeyRepositoryPort.class);
        rsaBssaService = mock(RsaBssaService.class);
        service = new BlindSignaturePublicKeyService(
            publicKeyRepository, privateKeyRepository, mock(RsaKeyMapper.class), rsaBssaService);

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();
        when(rsaBssaService.generateKeys()).thenReturn(keyPair);
        when(publicKeyRepository.existsByElectionId(electionId)).thenReturn(false);
    }

    @Test
    void registraLaClavePublicaAntesDeGuardarLaPrivada() {
        UUID keyId = UUID.randomUUID();
        when(publicKeyRepository.save(any()))
            .thenReturn(BlindSignaturePublicKeyModel.builder().id(keyId).electionId(electionId).build());

        service.generateKeys(electionId);

        InOrder order = inOrder(publicKeyRepository, privateKeyRepository);
        order.verify(publicKeyRepository).save(any());
        order.verify(privateKeyRepository).savePrivateKey(any(), any());
    }

    @Test
    void siOtraSolicitudYaRegistroLaClaveNoSeGuardaLaPrivada() {
        // Dos solicitudes simultáneas: la restricción única rechaza la segunda.
        when(publicKeyRepository.save(any()))
            .thenThrow(new DataIntegrityViolationException("unique_authorization_key_election"));

        assertThrows(CreatedKeyAlreadyExistsException.class, () -> service.generateKeys(electionId));
        verify(privateKeyRepository, never()).savePrivateKey(any(), any());
    }
}

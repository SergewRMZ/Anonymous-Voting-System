package com.voting_system.tally_service.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.dao.DataIntegrityViolationException;

import com.voting_system.tally_service.crypto.ElGamalKeyGenerator;
import com.voting_system.tally_service.exception.EncryptionKeysAlreadyExists;
import com.voting_system.tally_service.repository.EncryptionKeyRepositoryAdapter;
import com.voting_system.tally_service.repository.PrivateEncryptionKeyRepository;

class EncryptionKeyServiceTest {
    private EncryptionKeyRepositoryAdapter encryptionKeyRepository;
    private PrivateEncryptionKeyRepository privateKeyRepository;
    private ElGamalKeyGenerator keyGenerator;
    private EncryptionKeyService service;

    private final UUID electionId = UUID.randomUUID();
    private final byte[] publicKey = { 1, 2, 3 };
    private final byte[] privateKey = { 4, 5, 6 };

    @BeforeEach
    void setUp() {
        encryptionKeyRepository = mock(EncryptionKeyRepositoryAdapter.class);
        privateKeyRepository = mock(PrivateEncryptionKeyRepository.class);
        keyGenerator = mock(ElGamalKeyGenerator.class);
        service = new EncryptionKeyService(encryptionKeyRepository, privateKeyRepository, keyGenerator);

        when(encryptionKeyRepository.existsByElectionId(electionId)).thenReturn(false);
        when(keyGenerator.getPublicKey()).thenReturn(publicKey);
        when(keyGenerator.getPrivateKey()).thenReturn(privateKey);
    }

    @Test
    void registraLaClavePublicaAntesDeEscribirLaPrivada() {
        when(encryptionKeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.createEncryptionKeys(electionId);

        InOrder order = inOrder(encryptionKeyRepository, privateKeyRepository);
        order.verify(encryptionKeyRepository).save(any());
        order.verify(privateKeyRepository).savePrivateKey(electionId, privateKey);
    }

    @Test
    void siOtraSolicitudYaRegistroLaClaveNoSobrescribeLaPrivada() {
        // Dos solicitudes simultáneas: la restricción única rechaza la segunda, que no debe
        // reemplazar el archivo de la clave privada de la primera.
        when(encryptionKeyRepository.save(any()))
            .thenThrow(new DataIntegrityViolationException("unique_encryption_key_election"));

        assertThrows(EncryptionKeysAlreadyExists.class, () -> service.createEncryptionKeys(electionId));
        verify(privateKeyRepository, never()).savePrivateKey(eq(electionId), any());
    }
}

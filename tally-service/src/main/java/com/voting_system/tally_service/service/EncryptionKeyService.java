package com.voting_system.tally_service.service;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voting_system.tally_service.crypto.ElGamalKeyGenerator;
import com.voting_system.tally_service.exception.ElectionNotTalliedException;
import com.voting_system.tally_service.exception.EncryptionKeysAlreadyExists;
import com.voting_system.tally_service.exception.EncryptionKeysAlreadyExpiredException;
import com.voting_system.tally_service.repository.ElectionResultRepositoryPort;
import com.voting_system.tally_service.exception.EncryptionKeysNotFound;
import com.voting_system.tally_service.model.EncryptionKeyModel;
import com.voting_system.tally_service.model.KeyStatus;
import com.voting_system.tally_service.repository.EncryptionKeyRepositoryAdapter;
import com.voting_system.tally_service.repository.PrivateEncryptionKeyRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class EncryptionKeyService implements IEncryptionKeyService {
    private final EncryptionKeyRepositoryAdapter encryptionKeyRepository;
    private final PrivateEncryptionKeyRepository privateEncryptionKeyRepository;
    private final ElGamalKeyGenerator elGamalKeyGenerator;
    private final ElectionResultRepositoryPort electionResultRepository;

    /**
     * Registra la clave pública y después escribe la privada, en una sola transacción.
     * El orden importa: la clave privada se guarda en un archivo por elección, así que si dos
     * solicitudes simultáneas la escribieran antes de registrar la pública, la segunda
     * sobrescribiría la privada de la primera y la elección ya no podría contarse.
     */
    @Override
    @Transactional
    public EncryptionKeyModel createEncryptionKeys(UUID electionId) {
        if(encryptionKeyRepository.existsByElectionId(electionId)) {
            throw new EncryptionKeysAlreadyExists("Encryption keys already exists for this election: " + electionId);
        }

        // El generador es un bean compartido que guarda el último par generado: se genera y se
        // leen ambas claves en un solo bloque para no mezclar pares de dos elecciones simultáneas.
        byte[] publicKeyBytes;
        byte[] privateKeyBytes;
        synchronized (elGamalKeyGenerator) {
            elGamalKeyGenerator.generateKeys();
            publicKeyBytes = elGamalKeyGenerator.getPublicKey();
            privateKeyBytes = elGamalKeyGenerator.getPrivateKey();
        }
        String publicKey = Base64.getEncoder().encodeToString(publicKeyBytes);

        EncryptionKeyModel encryptionKeyModel = EncryptionKeyModel.builder()
            .electionId(electionId)
            .publicKey(publicKey)
            .status(KeyStatus.CREATED)

            .createdAt(Instant.now())
            .build();

        // La restricción única rechaza la segunda de dos solicitudes simultáneas.
        EncryptionKeyModel savedKey;
        try {
            savedKey = encryptionKeyRepository.save(encryptionKeyModel);
        } catch (DataIntegrityViolationException ex) {
            throw new EncryptionKeysAlreadyExists("Encryption keys already exists for this election: " + electionId);
        }

        // Si falla la escritura, la excepción revierte el registro de la clave pública.
        privateEncryptionKeyRepository.savePrivateKey(electionId, privateKeyBytes);
        return savedKey;
    }

    @Override 
    public EncryptionKeyModel activateEncryptionKeys(UUID electionId) {
        EncryptionKeyModel encryptionKeyModel = encryptionKeyRepository.findByElectionId(electionId)
            .orElseThrow(() -> new EncryptionKeysNotFound("Encryption keys not found for election with id: " + electionId));
        
        encryptionKeyModel.activate(); 
        return encryptionKeyRepository.save(encryptionKeyModel);
    }

    /**
     * Expira las claves de cifrado cuando la elección ya tiene resultados: a partir de ahí
     * la clave privada no se vuelve a usar. La clave pública se sigue pudiendo consultar.
     */
    @Override
    public EncryptionKeyModel expireEncryptionKeys(UUID electionId) {
        EncryptionKeyModel encryptionKeyModel = encryptionKeyRepository.findByElectionId(electionId)
            .orElseThrow(() -> new EncryptionKeysNotFound("Encryption keys not found for election with id: " + electionId));

        if (encryptionKeyModel.getStatus() == KeyStatus.EXPIRED) {
            throw new EncryptionKeysAlreadyExpiredException(electionId);
        }
        if (!electionResultRepository.existsByElectionId(electionId)) {
            throw new ElectionNotTalliedException(electionId);
        }

        encryptionKeyModel.expire(Instant.now());
        return encryptionKeyRepository.save(encryptionKeyModel);
    }

    @Override
    public EncryptionKeyModel getPublicEncryptionKey(UUID electionId) {
        return encryptionKeyRepository.findByElectionId(electionId)
            .orElseThrow(() -> new EncryptionKeysNotFound("Encryption keys not found for election with id: " + electionId));
    }
}


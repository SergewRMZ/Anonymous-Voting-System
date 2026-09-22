package com.voting_system.tally_service.service;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.voting_system.tally_service.dto.UpdateEncryptionKeyStatusDto;
import com.voting_system.tally_service.exception.EncryptionKeysAlreadyExists;
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
    private final ElGamalEncryption elGamalEncryption;

    @Override 
    public EncryptionKeyModel createEncryptionKeys(UUID electionId) {
        if(encryptionKeyRepository.existsByElectionId(electionId)) {
            throw new EncryptionKeysAlreadyExists("Encryption keys already exists for this election: " + electionId);
        }

        elGamalEncryption.generateKeys();
        String publicKey = Base64
            .getEncoder()
            .encodeToString(elGamalEncryption.getPublicKey());

        EncryptionKeyModel encryptionKeyModel = EncryptionKeyModel.builder()
            .electionId(electionId)
            .publicKey(publicKey)
            .status(KeyStatus.CREATED)
            .createdAt(Instant.now())
            .build();

        privateEncryptionKeyRepository.savePrivateKey(electionId, elGamalEncryption.getPrivateKey());
        return encryptionKeyRepository.save(encryptionKeyModel);
    }

    @Override 
    public EncryptionKeyModel setEncryptionKeyStatus(UUID electionId, UpdateEncryptionKeyStatusDto request) {
        EncryptionKeyModel encryptionKeyModel = encryptionKeyRepository.findByElectionId(electionId)
            .orElseThrow(() -> new EncryptionKeysNotFound("Encryption keys not found for election with id: " + electionId));
        
        encryptionKeyModel.activate(); 
        return encryptionKeyRepository.save(encryptionKeyModel);
    }
}

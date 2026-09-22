package com.voting_system.tally_service.mappers;

import org.springframework.stereotype.Component;

import com.voting_system.tally_service.entity.JpaEncryptionKeyEntity;
import com.voting_system.tally_service.model.EncryptionKeyModel;

@Component 
public class EncryptionKeyMapper {
    public JpaEncryptionKeyEntity toEntity(EncryptionKeyModel encryptionKeyModel) {
        return JpaEncryptionKeyEntity.builder()
            .encryptionKeyId(encryptionKeyModel.getEncryptionKeyId())
            .electionId(encryptionKeyModel.getElectionId())
            .publicKey(encryptionKeyModel.getPublicKey())
            .status(encryptionKeyModel.getStatus())
            .createdAt(encryptionKeyModel.getCreatedAt())
            .activatedAt(encryptionKeyModel.getActivatedAt())
            .expiredAt(encryptionKeyModel.getExpiredAt())
            .build();
    }

    public EncryptionKeyModel toModel(JpaEncryptionKeyEntity encryptionKeyEntity) {
        return EncryptionKeyModel.builder()
            .encryptionKeyId(encryptionKeyEntity.getEncryptionKeyId())
            .electionId(encryptionKeyEntity.getElectionId())
            .publicKey(encryptionKeyEntity.getPublicKey())
            .status(encryptionKeyEntity.getStatus())
            .createdAt(encryptionKeyEntity.getCreatedAt())
            .activatedAt(encryptionKeyEntity.getActivatedAt())
            .expiredAt(encryptionKeyEntity.getExpiredAt())
            .build();
    }
}

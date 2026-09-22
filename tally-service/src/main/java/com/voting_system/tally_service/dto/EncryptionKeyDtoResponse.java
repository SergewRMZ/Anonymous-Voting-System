package com.voting_system.tally_service.dto;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.voting_system.tally_service.model.EncryptionKeyModel;
import com.voting_system.tally_service.model.KeyStatus;

// Anotación para indicar que solo muestre los campos que no son NULL.
@JsonInclude (JsonInclude.Include.NON_NULL)
public record EncryptionKeyDtoResponse(
    UUID encryptionKeyId,
    UUID electionId,
    String publicKey,
    KeyStatus status,
    Instant createdAt,
    Instant activatedAt,
    Instant expiredAt
) {
    public static EncryptionKeyDtoResponse from(EncryptionKeyModel encryptionKeyModel) {
        return new EncryptionKeyDtoResponse(
            encryptionKeyModel.getEncryptionKeyId(), 
            encryptionKeyModel.getElectionId(), 
            encryptionKeyModel.getPublicKey(), 
            encryptionKeyModel.getStatus(), 
            encryptionKeyModel.getCreatedAt(), 
            encryptionKeyModel.getActivatedAt(), 
            encryptionKeyModel.getExpiredAt()
        );
    }
}

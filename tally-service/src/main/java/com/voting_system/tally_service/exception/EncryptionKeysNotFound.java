package com.voting_system.tally_service.exception;

public class EncryptionKeysNotFound extends RuntimeException {
    public EncryptionKeysNotFound(String message) {
        super(message);
    }
}

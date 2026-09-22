package com.voting_system.tally_service.exception;

public class EncryptionKeysAlreadyExists extends RuntimeException {
    public EncryptionKeysAlreadyExists(String message) {
        super(message);
    }
}

package com.voting_system.tally_service.exception;

import java.util.UUID;

/** Las claves de cifrado de la elección ya estaban expiradas. */
public class EncryptionKeysAlreadyExpiredException extends RuntimeException {
    public EncryptionKeysAlreadyExpiredException(UUID electionId) {
        super("Encryption keys of election " + electionId + " are already expired");
    }
}

package com.voting_system.tally_service.exception;

public class InvalidCiphertextException extends RuntimeException {
    public InvalidCiphertextException(String message) {
        super(message);
    }

    public InvalidCiphertextException(String message, Throwable cause) {
        super(message, cause);
    }
}

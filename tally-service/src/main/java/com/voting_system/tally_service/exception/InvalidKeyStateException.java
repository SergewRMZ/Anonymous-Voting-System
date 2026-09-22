package com.voting_system.tally_service.exception;

public class InvalidKeyStateException extends RuntimeException {
    public InvalidKeyStateException(String message) {
        super(message);
    }
}
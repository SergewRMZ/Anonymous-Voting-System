package com.voting_system.election_service.exceptions;

public class ElectionPositionNotFoundException extends RuntimeException {
    public ElectionPositionNotFoundException(String message) {
        super(message);
    }
}

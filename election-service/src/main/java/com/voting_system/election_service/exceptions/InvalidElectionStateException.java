package com.voting_system.election_service.exceptions;

public class InvalidElectionStateException extends RuntimeException {
    public InvalidElectionStateException(String message) {
        super(message);
    }
}

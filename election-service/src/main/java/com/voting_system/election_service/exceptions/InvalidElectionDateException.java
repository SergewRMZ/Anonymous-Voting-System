package com.voting_system.election_service.exceptions;

public class InvalidElectionDateException extends RuntimeException {
    public InvalidElectionDateException(String message) {
        super(message);
    }
}

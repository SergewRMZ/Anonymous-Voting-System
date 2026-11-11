package com.voting_system.election_service.exceptions;

public class CandidateLockedException extends RuntimeException {
    public CandidateLockedException (String message) {
        super(message);
    }
}

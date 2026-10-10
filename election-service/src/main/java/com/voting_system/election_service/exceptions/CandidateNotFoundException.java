package com.voting_system.election_service.exceptions;

public class CandidateNotFoundException extends RuntimeException {
    public CandidateNotFoundException (String message) {
        super(message);
    }
}

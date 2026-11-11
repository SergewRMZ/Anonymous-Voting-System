package com.voting_system.election_service.exceptions;

public class CandidatePhotoNotFoundException extends RuntimeException {
    public CandidatePhotoNotFoundException (String message) {
        super(message);
    }
}

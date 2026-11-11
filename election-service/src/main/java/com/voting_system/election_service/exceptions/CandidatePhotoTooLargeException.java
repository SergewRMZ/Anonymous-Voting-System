package com.voting_system.election_service.exceptions;

public class CandidatePhotoTooLargeException extends RuntimeException {
    public CandidatePhotoTooLargeException (String message) {
        super(message);
    }
}

package com.voting_system.election_service.exceptions;

public class InvalidCandidatePhotoException extends RuntimeException {
    public InvalidCandidatePhotoException (String message) {
        super(message);
    }
}

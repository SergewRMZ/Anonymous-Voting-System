package com.voting_system.election_service.exceptions;

public class UnsupportedCandidatePhotoTypeException extends RuntimeException {
    public UnsupportedCandidatePhotoTypeException (String message) {
        super(message);
    }
}

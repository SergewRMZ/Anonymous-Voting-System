package com.voting_system.election_service.exceptions;

import java.util.UUID;

public class ElectionNotFoundException extends RuntimeException {
    public ElectionNotFoundException(UUID electionId) {
        super("Election not found with ID: " + electionId);
    }
}

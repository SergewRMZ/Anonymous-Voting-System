package com.voting_system.tally_service.exception;

import java.util.UUID;

public class ElectionAlreadyTalliedException extends RuntimeException {
    public ElectionAlreadyTalliedException(UUID electionId) {
        super("Ya se ha computado el escrutinio de votos para la elección: " + electionId);
    }
}

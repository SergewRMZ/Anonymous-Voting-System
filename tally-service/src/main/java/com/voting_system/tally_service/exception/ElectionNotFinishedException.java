package com.voting_system.tally_service.exception;

import java.util.UUID;

public class ElectionNotFinishedException extends RuntimeException {
    public ElectionNotFinishedException(UUID electionId) {
        super("Aún no se ha solicitado el escrutinio de votos para la elección: " + electionId);
    }
}

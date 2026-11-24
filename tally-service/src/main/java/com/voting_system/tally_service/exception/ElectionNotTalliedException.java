package com.voting_system.tally_service.exception;

import java.util.UUID;

/** La elección todavía no tiene resultados: la clave privada aún se necesita para el conteo. */
public class ElectionNotTalliedException extends RuntimeException {
    public ElectionNotTalliedException(UUID electionId) {
        super("Election " + electionId + " has not been tallied yet, its encryption keys cannot be expired");
    }
}

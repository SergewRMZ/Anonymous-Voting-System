package com.voting_system.tally_service.exception;

import java.util.UUID;

/**
 * Exception retornada si se solicita realizar el conteo de votos
 * antes de que la elección cierre o haya finalizado la recepción
 * de votos en la urna digital.
 * ElectionNotClosedException
 */
public class ElectionNotClosedException extends RuntimeException {
    public ElectionNotClosedException(UUID electionId) {
        super("La elección " + electionId + " aún no ha finalizado");
    }
}

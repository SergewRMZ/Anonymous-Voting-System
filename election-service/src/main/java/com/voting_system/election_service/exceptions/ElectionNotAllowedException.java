package com.voting_system.election_service.exceptions;

import java.util.UUID;

/**
 * Exception que es retornada si un usuario no tiene los permisos
 * suficientes para ver los detalles de la elección. 
 * ElectionNotAllowedException
 */
public class ElectionNotAllowedException extends RuntimeException {
    public ElectionNotAllowedException(UUID electionId) {
        super("This user doesn't have permission to view election with ID: " + electionId);
    }
}

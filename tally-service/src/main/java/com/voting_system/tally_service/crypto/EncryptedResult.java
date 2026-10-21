package com.voting_system.tally_service.crypto;

import java.util.UUID;

/**
 * Objeto que representa la suma de todos los puntos cifrados que representa
 * a cada voto de la urna digital.
 * EncryptedResult
 * @param electionId Identificador de la elección.
 * @param electionPositionId Identificador de un registro que relaciona una elección con un cargo electoral
 * @param candidacyId Identificador de una candidatura
 * @param c1
 * @param c2
 */
public record EncryptedResult(
    UUID electionId,
    UUID electionPositionId,
    UUID candidacyId,
    byte[] c1,
    byte[] c2,
    int ballotCount
) {} 

package com.voting_system.election_service.dto;

import java.util.UUID;

/**
 * Estructura utilizada como objeto para almacenar el identificador de 
 * la posición junto con la candidatura durante la consulta realizada por el
 * servicio de conteo de votos.
 * ElectionStructureRowDto
 * @param electionPositionId Identificador que relaciona una elección con una posición electoral.
 * @param candidacyId Identificador de una candidatura de la elección.
 */
public record ElectionStructureRowDto(
    UUID electionPositionId,
    UUID candidacyId
) {}

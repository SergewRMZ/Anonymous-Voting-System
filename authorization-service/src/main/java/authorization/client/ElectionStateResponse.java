package authorization.client;

import java.util.UUID;

/** Respuesta de {@code GET /api/election-service/elections/{electionId}/state}. */
public record ElectionStateResponse(
    UUID electionId,
    ElectionStatus status
) {}

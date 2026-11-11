package authorization.dto;

import java.util.UUID;

/**
 * Indica si el votante autenticado ya solicitó la firma ciega de su voto en una elección.
 * Como solo se emite una firma por votante y elección, un votante autorizado ya no puede
 * obtener otra y, por lo tanto, no puede volver a votar.
 *
 * @param electionId Identificador de la elección.
 * @param authorized true si el votante ya obtuvo su firma ciega en esta elección.
 */
public record AuthorizationStatusResponse(
    UUID electionId,
    boolean authorized
) {}

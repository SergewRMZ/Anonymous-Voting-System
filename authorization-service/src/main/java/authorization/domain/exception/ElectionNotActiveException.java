package authorization.domain.exception;

import java.util.UUID;

/** La elección no está en periodo de votación: no se emiten firmas ciegas. */
public class ElectionNotActiveException extends RuntimeException {
    public ElectionNotActiveException(UUID electionId) {
        super("Election " + electionId + " is not active, blind signatures cannot be issued");
    }
}

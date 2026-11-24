package authorization.domain.exception;

import java.util.UUID;

/** No se pudo consultar el estado de la elección en election-service. */
public class ElectionStateUnavailableException extends RuntimeException {
    public ElectionStateUnavailableException(UUID electionId) {
        super("The status of election " + electionId + " could not be retrieved");
    }

    public ElectionStateUnavailableException(UUID electionId, Throwable cause) {
        super("The status of election " + electionId + " could not be retrieved", cause);
    }
}

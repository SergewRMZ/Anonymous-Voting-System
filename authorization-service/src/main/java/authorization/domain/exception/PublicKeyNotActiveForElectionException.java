package authorization.domain.exception;

import java.util.UUID;

public class PublicKeyNotActiveForElectionException extends RuntimeException {
    public PublicKeyNotActiveForElectionException(UUID electionId) {
        super("Public key active hasn't found for electionId: " + electionId);
    }
}

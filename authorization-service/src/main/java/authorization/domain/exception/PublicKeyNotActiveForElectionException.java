package authorization.domain.exception;

import java.util.UUID;

public class PublicKeyNotActiveForElectionException extends RuntimeException {
    public PublicKeyNotActiveForElectionException(UUID electionId) {
        super("No active public key found for electionId: " + electionId);
    }
}

package authorization.domain.exception;

import java.util.UUID;

public class ActiveKeyAlreadyExistsException extends RuntimeException {
    public ActiveKeyAlreadyExistsException(UUID electionId) {
        super("An active key already exists for this election: " + electionId);
    }
}

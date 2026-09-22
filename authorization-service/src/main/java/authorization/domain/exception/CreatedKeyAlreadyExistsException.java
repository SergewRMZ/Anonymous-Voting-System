package authorization.domain.exception;

import java.util.UUID;

public class CreatedKeyAlreadyExistsException extends RuntimeException {
    public CreatedKeyAlreadyExistsException(UUID electionId) {
        super("An rsa key-pair already exists for this election: " + electionId);
    }
}

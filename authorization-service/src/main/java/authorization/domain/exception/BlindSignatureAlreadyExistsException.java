package authorization.domain.exception;

import java.util.UUID;

public class BlindSignatureAlreadyExistsException extends RuntimeException {
    public BlindSignatureAlreadyExistsException(UUID userId, UUID electionId) {
        super("User " + userId + " already has a blind signature for election with ID " + electionId);
    }
}

package authorization.domain.exception;

import java.util.UUID;

public class BlindSignatureAlreadyExistsException extends RuntimeException {
    public BlindSignatureAlreadyExistsException(UUID userId, UUID electionId) {
        super("User already has a blind signature for election with ID " + electionId);
    }
}

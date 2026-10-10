package authorization.domain.exception;

import java.util.UUID;

public class SigningKeysNotLoadedException extends RuntimeException {
    public SigningKeysNotLoadedException(UUID electionId) {
        super("Signing keys hasn't not loaded for Election ID: " + electionId);
    }
}

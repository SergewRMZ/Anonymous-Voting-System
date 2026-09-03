package authorization.domain.exception;

import java.util.UUID;

public class PublicKeyNotFoundException extends RuntimeException {
    public PublicKeyNotFoundException(UUID electionId) {
        super("Authorization public key has not found for election ID: " + electionId);
    }
}

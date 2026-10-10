package authorization.domain.exception;

public class PublicKeyNotFoundException extends RuntimeException {
    public PublicKeyNotFoundException(String message) {
        super(message);
    }
}

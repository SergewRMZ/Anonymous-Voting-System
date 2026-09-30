package authorization.config;

import authorization.domain.exception.*;

import org.springframework.http.ProblemDetail;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CreatedKeyAlreadyExistsException.class)
    public ProblemDetail handleCreatedKeyAlreadyExistsException(CreatedKeyAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE, 
            ex.getMessage()
        );

        problemDetail.setTitle("Created Key Conflict");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (PublicKeyNotActiveForElectionException.class)
    public ProblemDetail handlePublicKeyNotActiveForElectionException(PublicKeyNotActiveForElectionException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, 
            ex.getMessage()
        );

        problemDetail.setTitle("Public Key Not Active for Election");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (PublicKeyNotFoundException.class)
    public ProblemDetail handlePublicKeyNotFoundException(PublicKeyNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, 
            ex.getMessage()
        );

        problemDetail.setTitle("Public Key Not Found for Election");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (SigningKeysNotLoadedException.class)
    public ProblemDetail handleSigningKeysNotLoaded(SigningKeysNotLoadedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, 
            ex.getMessage()
        );

        problemDetail.setTitle("Authorization keys hasn't loaded");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (BlindSignatureAlreadyExistsException.class)
    public ProblemDetail handleBlindSignatureAlreadyExistsException(BlindSignatureAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, 
            ex.getMessage()
        );

        problemDetail.setTitle("The user has already request a blind signature");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}


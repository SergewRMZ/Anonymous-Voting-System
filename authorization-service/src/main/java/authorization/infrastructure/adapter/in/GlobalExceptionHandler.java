package authorization.infrastructure.adapter.in;

import authorization.domain.exception.CreatedKeyAlreadyExistsException;
import authorization.domain.exception.PublicKeyNotActiveForElectionException;

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
            HttpStatus.CONFLICT, 
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
}

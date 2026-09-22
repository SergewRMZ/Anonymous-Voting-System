package com.voting_system.tally_service.config;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.voting_system.tally_service.exception.*;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    @ExceptionHandler(EncryptionKeysAlreadyExists.class)
    public ProblemDetail handleEncryptionKeysAlreadyExists(EncryptionKeysAlreadyExists ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());

        problemDetail.setTitle("Encryption Keys Already Exists");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (EncryptionKeysNotFound.class)
    public ProblemDetail handleEncryptionKeysNotFound(EncryptionKeysNotFound ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());

        problemDetail.setTitle("Encryption Keys Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (InvalidKeyStateException.class)
    public ProblemDetail handleInvalidKeyStateException(InvalidKeyStateException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());

        problemDetail.setTitle("Invalid Key Status");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}

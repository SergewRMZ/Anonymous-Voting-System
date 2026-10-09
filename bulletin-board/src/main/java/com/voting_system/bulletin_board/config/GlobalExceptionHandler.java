package com.voting_system.bulletin_board.config;

import org.springframework.http.ProblemDetail;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.voting_system.bulletin_board.exceptions.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RSAPublicKeyException.class)
    public ProblemDetail handleCreatedKeyAlreadyExistsException(RSAPublicKeyException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, 
            ex.getMessage()
        );

        problemDetail.setTitle("Invalid RSA Public Key");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (CryptographicOperationException.class)
    public ProblemDetail handleCryptographicOperationException(CryptographicOperationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            ex.getMessage()
        );

        problemDetail.setTitle("Internal Cryptographic Error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}



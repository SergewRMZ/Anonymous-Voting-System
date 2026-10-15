package com.voting_system.bulletin_board.config;

import org.springframework.http.ProblemDetail;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.voting_system.bulletin_board.exceptions.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Para errores al reconstruir la clave pública de RSA
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

    // Para errores de verificación del voto.
    @ExceptionHandler (VoteVerificationException.class)
    public ProblemDetail handleVoteVerificationException(VoteVerificationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());

        problemDetail.setTitle("ERROR VOTE VALIDATION");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // Para errores durante las operaciones criptográficas.
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

    @ExceptionHandler (DuplicateVoteException.class)
    public ProblemDetail handleDuplicateVoteException(DuplicateVoteException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, 
            ex.getMessage()
        );

        problemDetail.setTitle("Duplicate Vote");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}



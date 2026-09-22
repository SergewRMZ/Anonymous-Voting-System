package com.voting_system.authentication_service.config;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.voting_system.authentication_service.exceptions.InternalServerErrorException;
import com.voting_system.authentication_service.exceptions.UsernameOrEmailAlreadyExistsException;
@RestControllerAdvice 
public class GlobalExceptionHandler {
    @ExceptionHandler(UsernameOrEmailAlreadyExistsException.class)
    public ProblemDetail handleUsernameOrEmailAlreadyExists(UsernameOrEmailAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );

        problemDetail.setTitle("Username or email already exists");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (InternalServerErrorException.class)
    public ProblemDetail handleInternalServerErrorException(InternalServerErrorException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            ex.getMessage()
        );

        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}

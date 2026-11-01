package com.voting_system.authentication_service.config;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.voting_system.authentication_service.exceptions.InternalServerErrorException;
import com.voting_system.authentication_service.exceptions.UserNotFoundException;
import com.voting_system.authentication_service.exceptions.UsernameOrEmailAlreadyExistsException;
import com.voting_system.authentication_service.exceptions.VoterApprovalException;
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

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(UserNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );

        problemDetail.setTitle("User Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(VoterApprovalException.class)
    public ProblemDetail handleVoterApprovalException(VoterApprovalException ex) {
        HttpStatus status = ex.getReason() == VoterApprovalException.Reason.INVALID_MATRICULA
            ? HttpStatus.UNPROCESSABLE_CONTENT
            : HttpStatus.CONFLICT;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());

        problemDetail.setTitle("Voter Approval Rejected");
        problemDetail.setProperty("code", ex.getReason().name());
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

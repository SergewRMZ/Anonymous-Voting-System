package com.voting_system.election_service.config;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.voting_system.election_service.exceptions.InvalidElectionStateException;

public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidElectionStateException.class)
    public ProblemDetail handelInvalidElectionStateException(InvalidElectionStateException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());

        problemDetail.setTitle("Election State Error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}

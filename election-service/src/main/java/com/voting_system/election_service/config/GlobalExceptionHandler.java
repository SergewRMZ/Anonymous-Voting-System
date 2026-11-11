package com.voting_system.election_service.config;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.voting_system.election_service.exceptions.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidElectionStateException.class)
    public ProblemDetail handelInvalidElectionStateException(InvalidElectionStateException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());

        problemDetail.setTitle("Election State Error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(InvalidElectionDateException.class)
    public ProblemDetail handleInvalidElectionDateException(InvalidElectionDateException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ex.getMessage());

        problemDetail.setTitle("Invalid Election Dates");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (ElectionNotFoundException.class)
    public ProblemDetail handleElectionNotFoundException(ElectionNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());

        problemDetail.setTitle("Election Not Found ");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (PositionNotFoundException.class)
    public ProblemDetail handlePositionNotFoundException(PositionNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());

        problemDetail.setTitle("Electoral Position Not Found ");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (CandidateNotFoundException.class)
    public ProblemDetail handleCandidateNotFoundException(CandidateNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());

        problemDetail.setTitle("Candidate Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (ElectionPositionNotFoundException.class)
    public ProblemDetail handleElectionPositionNotFoundException(ElectionPositionNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());

        problemDetail.setTitle("Election Position Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (CandidatePhotoNotFoundException.class)
    public ProblemDetail handleCandidatePhotoNotFoundException(CandidatePhotoNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());

        problemDetail.setTitle("Candidate Photo Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (InvalidCandidatePhotoException.class)
    public ProblemDetail handleInvalidCandidatePhotoException(InvalidCandidatePhotoException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ex.getMessage());

        problemDetail.setTitle("Invalid Candidate Photo");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (CandidatePhotoTooLargeException.class)
    public ProblemDetail handleCandidatePhotoTooLargeException(CandidatePhotoTooLargeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONTENT_TOO_LARGE, ex.getMessage());

        problemDetail.setTitle("Candidate Photo Too Large");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (UnsupportedCandidatePhotoTypeException.class)
    public ProblemDetail handleUnsupportedCandidatePhotoTypeException(UnsupportedCandidatePhotoTypeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());

        problemDetail.setTitle("Unsupported Candidate Photo Type");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler (CandidateLockedException.class)
    public ProblemDetail handleCandidateLockedException(CandidateLockedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());

        problemDetail.setTitle("Candidate Locked");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // Lo lanza Spring antes de llegar al controlador, si el archivo excede spring.servlet.multipart.max-file-size.
    @ExceptionHandler (MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONTENT_TOO_LARGE, "The uploaded file exceeds the maximum allowed size");

        problemDetail.setTitle("Candidate Photo Too Large");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}

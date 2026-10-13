package com.voting_system.tally_service.exception;

public class ElectionNotClosedException extends RuntimeException {
    public ElectionNotClosedException() {
        super("Election must be CLOSED before tallying");
    }
}

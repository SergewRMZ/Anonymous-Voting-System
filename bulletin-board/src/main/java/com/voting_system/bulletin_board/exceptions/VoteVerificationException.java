package com.voting_system.bulletin_board.exceptions;

public class VoteVerificationException extends RuntimeException {
    public VoteVerificationException() {
        super("Unable to process vote");
    }
}

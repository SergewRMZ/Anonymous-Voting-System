package com.voting_system.bulletin_board.exceptions;

public class ElectionNotActiveException extends RuntimeException {
    public ElectionNotActiveException() {
        super("Election is not active, votes cannot be submitted");
    }
}

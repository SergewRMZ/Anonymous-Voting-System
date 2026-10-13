package com.voting_system.bulletin_board.exceptions;

public class ElectionNotOpenException extends RuntimeException {
    public ElectionNotOpenException() {
        super("Election is not currently accepting votes");
    }
}

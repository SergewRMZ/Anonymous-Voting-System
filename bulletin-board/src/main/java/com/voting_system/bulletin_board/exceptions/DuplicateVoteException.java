package com.voting_system.bulletin_board.exceptions;

public class DuplicateVoteException extends RuntimeException {
    public DuplicateVoteException() {
        super("Vote has already been registered previously");
    }
}

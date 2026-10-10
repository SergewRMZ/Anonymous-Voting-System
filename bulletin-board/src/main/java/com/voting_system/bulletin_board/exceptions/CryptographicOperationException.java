package com.voting_system.bulletin_board.exceptions;

public class CryptographicOperationException extends RuntimeException {
    public CryptographicOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

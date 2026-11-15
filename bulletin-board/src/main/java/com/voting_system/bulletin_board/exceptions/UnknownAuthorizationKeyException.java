package com.voting_system.bulletin_board.exceptions;

/**
 * El voto dice estar firmado con una clave que no es la clave de autorización de su elección.
 */
public class UnknownAuthorizationKeyException extends RuntimeException {
    public UnknownAuthorizationKeyException() {
        super("The vote was not signed with the authorization key of this election");
    }
}

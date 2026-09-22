package com.voting_system.authentication_service.exceptions;

public class UsernameOrEmailAlreadyExistsException extends RuntimeException {
    public UsernameOrEmailAlreadyExistsException() {
        super("A user already exists with this username or email");
    }
}

package com.voting_system.authentication_service.exceptions;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException() {
        super("An unexpected internal server error ocurred");
    }
}

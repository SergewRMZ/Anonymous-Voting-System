package com.voting_system.election_service.exceptions;

public class PositionAlreadyExistsException extends RuntimeException {
    public PositionAlreadyExistsException(String positionName) {
        super("An electoral position with this name already exists: " + positionName);
    }
}

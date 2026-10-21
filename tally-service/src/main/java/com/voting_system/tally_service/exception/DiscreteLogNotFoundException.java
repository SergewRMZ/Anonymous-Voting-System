package com.voting_system.tally_service.exception;

public class DiscreteLogNotFoundException extends RuntimeException {
    public DiscreteLogNotFoundException() {
        super("The discrete logarithm could not be found within the specified range");
    }
}

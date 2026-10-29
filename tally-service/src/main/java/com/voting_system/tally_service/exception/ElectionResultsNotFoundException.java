package com.voting_system.tally_service.exception;

import java.util.UUID;

public class ElectionResultsNotFoundException extends RuntimeException {
    public ElectionResultsNotFoundException(UUID electionId) {
        super("No results found for election ID:" + electionId);
    }
}

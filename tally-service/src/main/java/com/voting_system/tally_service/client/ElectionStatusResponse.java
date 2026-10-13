package com.voting_system.tally_service.client;

import java.util.UUID;

public record ElectionStatusResponse(
    UUID electionId,
    String status,
    boolean acceptingVotes
) {}

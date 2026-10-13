package com.voting_system.bulletin_board.client;

import java.util.UUID;

public record ElectionStatusResponse(
    UUID electionId,
    String status,
    boolean acceptingVotes
) {}

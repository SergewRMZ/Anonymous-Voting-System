package com.voting_system.bulletin_board.client;

import java.util.UUID;

public record GetElectionStatusDto (
    UUID electionId,
    ElectionStatus status
) {}

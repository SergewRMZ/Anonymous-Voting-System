package com.voting_system.bulletin_board.repository;

import java.util.UUID;

public record VoteBallotProjection(
    UUID id,
    UUID electionId,
    UUID electionPositionId,
    UUID candidacyId,
    String c1,
    String c2
) {}

package com.voting_system.bulletin_board.model;

import java.util.UUID;
import java.util.List;

public record Position (
    UUID electionPositionId,
    List<Ballot> ballots
) {}

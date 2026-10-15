package com.voting_system.tally_service.client.dto;

import java.util.UUID;
import java.util.List;

public record Position (
    UUID electionPositionId,
    List<Ballot> ballots
) {}

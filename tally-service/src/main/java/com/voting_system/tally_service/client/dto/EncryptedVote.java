package com.voting_system.tally_service.client.dto;

import java.util.List;
import java.util.UUID;

public record EncryptedVote(
    UUID authorizationKeysId,
    List<Position> positions
) {}

package com.voting_system.bulletin_board.model;

import java.util.List;
import java.util.UUID;

public record EncryptedVote(
    UUID authorizationKeysId,
    List<Position> positions
) {}

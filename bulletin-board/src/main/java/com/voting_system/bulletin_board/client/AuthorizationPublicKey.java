package com.voting_system.bulletin_board.client;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuthorizationPublicKey(
    UUID authorizationKeysId,
    UUID electionId,
    Map<String, Object> publicKey,
    KeyStatus keyStatus,
    Instant createdAt,
    Instant activatedAt,
    Instant expiredAt
) {
}

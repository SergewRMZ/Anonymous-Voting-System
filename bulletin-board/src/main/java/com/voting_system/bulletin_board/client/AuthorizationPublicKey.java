package com.voting_system.bulletin_board.client;

import java.time.Instant;
import java.util.UUID;

public record AuthorizationPublicKey(
    UUID authorizationKeysId,
    UUID electionId,
    RsaPublicKey publicKey,
    KeyStatus keyStatus,
    Instant createdAt,
    Instant activatedAt,
    Instant expiredAt
) {
    public record RsaPublicKey(
        String kty,
        String e,
        String use,
        String kid, 
        String n
    ) {}
}

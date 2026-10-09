package com.voting_system.bulletin_board.client.authorization.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.voting_system.bulletin_board.client.authorization.model.KeyStatus;

public record BlindSignaturePublicKeyResponse(
    UUID authorizationKeysId,
    UUID electionId,
    Map<String, Object> publicKey,
    KeyStatus keyStatus,
    Instant createdAt,
    Instant activatedAt,
    Instant expiredAt
) {
}
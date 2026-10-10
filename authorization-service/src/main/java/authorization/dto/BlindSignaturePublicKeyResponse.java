package authorization.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import authorization.domain.model.KeyStatus;

@JsonInclude (JsonInclude.Include.NON_NULL)
public record BlindSignaturePublicKeyResponse(
    UUID authorizationKeysId,
    UUID electionId,
    Map<String, Object> publicKey,
    KeyStatus keyStatus,
    Instant createdAt,
    Instant activatedAt,
    Instant expiredAt
) {}
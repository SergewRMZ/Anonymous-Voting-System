package authorization.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import authorization.domain.model.ElectionPublicKeyModel;
import authorization.domain.model.KeyStatus;
import authorization.infrastructure.adapter.out.mappers.JwkMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@JsonInclude (JsonInclude.Include.NON_NULL)
public class ElectionPublicKeyResponse {
    private UUID authorizationKeysId;
    private UUID electionId;
    private Map<String, Object> publicKey;
    private KeyStatus keyStatus;
    private Instant createdAt;
    private Instant activatedAt;
    private Instant expiredAt;

    public static ElectionPublicKeyResponse from (ElectionPublicKeyModel model) {
        return ElectionPublicKeyResponse.builder()
            .authorizationKeysId(model.getAuthorizationKeysId())
            .electionId(model.getElectionId())
            .publicKey(JwkMapper.bytesToJwk(model.getPublicKey()))
            .keyStatus(model.getStatus())
            .createdAt(model.getCreatedAt())
            .activatedAt(model.getActivatedAt())
            .expiredAt(model.getExpiredAt())
            .build();
    }
}

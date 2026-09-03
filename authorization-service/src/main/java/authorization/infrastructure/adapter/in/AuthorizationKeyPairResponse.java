package authorization.infrastructure.adapter.in;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import authorization.domain.model.AuthorizationPublicKeyModel;
import authorization.domain.model.KeyStatus;
import authorization.infrastructure.adapter.out.JwkMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthorizationKeyPairResponse {
    private UUID authorizationKeysId;
    private UUID electionId;
    private Map<String, Object> publicKey;
    private KeyStatus keyStatus;
    private Instant createdAt;

    public static AuthorizationKeyPairResponse from (AuthorizationPublicKeyModel model) {
        return AuthorizationKeyPairResponse.builder()
            .authorizationKeysId(model.getAuthorizationKeysId())
            .electionId(model.getElectionId())
            .publicKey(JwkMapper.bytesToJwk(model.getPublicKey()))
            .keyStatus(model.getStatus())
            .createdAt(model.getCreatedAt())
            .build();
    }
}

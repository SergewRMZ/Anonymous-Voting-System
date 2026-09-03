package authorization.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorizationPublicKeyModel {
    private UUID authorizationKeysId;
    private UUID electionId;
    private byte[] publicKey;
    private KeyStatus status;
    private Instant createdAt;
}

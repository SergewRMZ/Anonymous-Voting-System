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
public class ElectionPublicKeyModel {
    private UUID authorizationKeysId;
    private UUID electionId;
    private byte[] publicKey;
    private KeyStatus status;
    private Instant createdAt;
    private Instant activatedAt;
    private Instant expiredAt;

    public void activate(Instant activatedAt) {
        if(this.status != KeyStatus.CREATED) {
            throw new IllegalStateException("Only keys with status CREATED can be activated.");
        }

        this.status = KeyStatus.ACTIVE;
        this.activatedAt = activatedAt;
    }

    public void expire(Instant expiredAt) {
        if(this.status != KeyStatus.ACTIVE) {
            throw new IllegalStateException("Only keys with status ACTIVE can be expired.");
        }
        this.status = KeyStatus.EXPIRED;
        this.expiredAt = expiredAt;
    }
}

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
public class BlindSignaturePublicKeyModel {
    private UUID id;
    private UUID electionId;
    private String publicKey;
    private KeyStatus status;
    private Instant createdAt;
    private Instant activatedAt;
    private Instant expiredAt;

    public void create() {
        this.status = KeyStatus.CREATED;
        this.createdAt = Instant.now();
    }

    public void activate() {
        if(this.status != KeyStatus.CREATED) {
            throw new IllegalStateException("Only keys with status CREATED can be activated.");
        }

        this.status = KeyStatus.ACTIVE;
        this.activatedAt = Instant.now();
    }

    public void expire() {
        if(this.status != KeyStatus.ACTIVE) {
            throw new IllegalStateException("Only keys with status ACTIVE can be expired.");
        }
        this.status = KeyStatus.EXPIRED;
        this.expiredAt = Instant.now();
    }
}

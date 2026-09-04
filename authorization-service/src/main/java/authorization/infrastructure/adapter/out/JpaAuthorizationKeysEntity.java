package authorization.infrastructure.adapter.out;

import java.time.Instant;
import java.util.UUID;

import authorization.domain.model.KeyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "authorization_keys")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JpaAuthorizationKeysEntity {
    @Id
    @GeneratedValue
    private UUID authorizationKeyId;

    @Column(nullable = false)
    private UUID electionId;

    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] publicKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeyStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = true)
    private Instant activatedAt;

    @Column(nullable = true)
    private Instant expiredAt;  
}

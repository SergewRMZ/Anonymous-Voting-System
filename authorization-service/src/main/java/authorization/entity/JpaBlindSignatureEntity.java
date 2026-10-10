package authorization.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Table (
    name = "blind_signatures",
    uniqueConstraints = {
        @UniqueConstraint (
            name = "unique_blind_signature",
            columnNames = {"election_id", "user_id"}
        )
    }
)
@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class JpaBlindSignatureEntity {
    @Id 
    @GeneratedValue 
    private UUID id;

    @Column (nullable = false)
    private UUID electionId;

    @Column (nullable = false)
    private UUID userId;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "authorization_keys_id", nullable = false)
    private JpaBlindSignaturePublicKeyEntity authorizationKeysId;

    @Column (nullable = false, columnDefinition = "TEXT")
    private String blindSignature;

    @Column (nullable = false)
    private Instant createdAt;
}

package com.voting_system.tally_service.entity;

import java.time.Instant;
import java.util.UUID;

import com.voting_system.tally_service.model.KeyStatus;

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

@Entity 
@Table (name = "encryption_keys")
@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class JpaEncryptionKeyEntity {
    @Id 
    @GeneratedValue 
    private UUID encryptionKeyId;

    @Column (nullable = false)
    private UUID electionId;

    @Column (nullable = false)
    private String publicKey;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false)
    private KeyStatus status;

    @Column (nullable = false)
    private Instant createdAt;

    /** This fields can be null. */
    private Instant activatedAt;
    private Instant expiredAt;
}

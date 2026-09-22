package com.voting_system.tally_service.model;

import java.time.Instant;
import java.util.UUID;
import com.voting_system.tally_service.exception.InvalidKeyStateException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class EncryptionKeyModel {
    private UUID encryptionKeyId;
    private UUID electionId;
    private String publicKey;
    private KeyStatus status;
    private Instant createdAt;
    private Instant activatedAt;
    private Instant expiredAt;

    public void activate() {
        if(this.status == KeyStatus.ACTIVE) 
            throw new InvalidKeyStateException("This keys has already been activated");
        
        if(this.status != KeyStatus.CREATED) 
            throw new InvalidKeyStateException("Only keys with status CREATED can be activated"); 
        

        this.status = KeyStatus.ACTIVE;
        this.activatedAt = Instant.now();
    }

    public void expire(Instant expiredAt) {
        if(this.status != KeyStatus.ACTIVE) {
            throw new InvalidKeyStateException("Only keys with status ACTIVE can be expired");
        }

        this.status = KeyStatus.ACTIVE;
        this.expiredAt = expiredAt;
    }
}

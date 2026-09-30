package authorization.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class BlindSignatureModel {
    private UUID id;
    private UUID electionId;
    private UUID userId;
    private UUID authorizationKeysId;
    private String blindSignature; 
    private Instant createdAt;
}

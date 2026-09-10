package authorization.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
@Builder 
public class AuthorizedVoterModel {
    private UUID electionId;
    private UUID userId;
    private UUID authorizationKeysId;
    private String blindSignature; 
    private Instant issuedAt;
}

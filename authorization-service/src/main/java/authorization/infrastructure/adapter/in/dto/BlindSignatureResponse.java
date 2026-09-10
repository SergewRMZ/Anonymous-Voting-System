package authorization.infrastructure.adapter.in.dto;

import java.util.UUID;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import authorization.domain.model.AuthorizedVoterModel;
@Getter 
@AllArgsConstructor 
@Builder 
public class BlindSignatureResponse {
    private UUID electionId;
    private UUID authorizationKeysId;
    private String blindSignature;
    private Instant issuedAt;

    public static BlindSignatureResponse from(AuthorizedVoterModel authorizedVoterModel) {
        return new BlindSignatureResponse(
            authorizedVoterModel.getElectionId(),
            authorizedVoterModel.getAuthorizationKeysId(),
            authorizedVoterModel.getBlindSignature(),
            authorizedVoterModel.getIssuedAt()
        );
    }
}
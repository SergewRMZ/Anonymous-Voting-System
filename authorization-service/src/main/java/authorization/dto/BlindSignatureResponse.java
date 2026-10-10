package authorization.dto;

import java.util.UUID;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import authorization.domain.model.BlindSignatureModel;
@Getter 
@AllArgsConstructor 
@Builder 
public class BlindSignatureResponse {
    private UUID authoritizationKeysId;
    private UUID electionId;
    private UUID userId;
    private String blindSignature;
    private Instant issuedAt;

    public static BlindSignatureResponse from(BlindSignatureModel blindSignatureModel) {
        return new BlindSignatureResponse(
            blindSignatureModel.getId(),
            blindSignatureModel.getElectionId(),
            blindSignatureModel.getUserId(),
            blindSignatureModel.getBlindSignature(),
            blindSignatureModel.getCreatedAt()
        );
    }
}
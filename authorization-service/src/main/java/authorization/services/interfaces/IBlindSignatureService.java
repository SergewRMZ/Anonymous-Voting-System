package authorization.services.interfaces;

import java.util.UUID;

import authorization.domain.model.BlindSignatureModel;
import authorization.dto.BlindSignatureRequest;

public interface IBlindSignatureService {
    public BlindSignatureModel generateBlindSignature(UUID electionId, UUID userId, BlindSignatureRequest request);
    public boolean hasRequestedAuthorization(UUID electionId, UUID userId);
}

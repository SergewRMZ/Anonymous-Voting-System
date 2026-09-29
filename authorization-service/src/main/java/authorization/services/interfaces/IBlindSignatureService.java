package authorization.services.interfaces;

import java.util.UUID;

import authorization.domain.model.BlindSignatureModel;
import authorization.dto.BlindSignatureRequest;

public interface IBlindSignatureService {
    public BlindSignatureModel generateBlindSignature(UUID electionId, BlindSignatureRequest request);
}

package authorization.application.port.in;
import java.util.UUID;

import authorization.domain.model.ElectionPublicKeyModel;

public interface CreateAuthorizationKeysUseCase {
    ElectionPublicKeyModel generateKeyPair(UUID electionId);
}

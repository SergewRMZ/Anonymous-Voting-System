package authorization.application.port.in;
import java.util.UUID;

import authorization.domain.model.AuthorizationPublicKeyModel;

public interface CreateAuthorizationKeysUseCase {
    AuthorizationPublicKeyModel generateKeyPair(UUID electionId);
}

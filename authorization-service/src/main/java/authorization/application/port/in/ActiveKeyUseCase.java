package authorization.application.port.in;

import java.util.UUID;

import authorization.domain.model.ElectionPublicKeyModel;

public interface ActiveKeyUseCase {
    public ElectionPublicKeyModel activateKey(UUID electionId);
}

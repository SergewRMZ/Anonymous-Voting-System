package authorization.application.port.out;

import java.util.Optional;
import java.util.UUID;

import authorization.domain.model.AuthorizationPublicKeyModel;

public interface KeyRepositoryPort {
    public AuthorizationPublicKeyModel save(AuthorizationPublicKeyModel keyPair);
    public Optional<AuthorizationPublicKeyModel> findByElectionId(UUID electionId);

}

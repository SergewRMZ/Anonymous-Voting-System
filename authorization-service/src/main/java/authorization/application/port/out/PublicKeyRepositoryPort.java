package authorization.application.port.out;

import java.util.Optional;
import java.util.UUID;

import authorization.domain.model.AuthorizationPublicKeyModel;
import authorization.domain.model.KeyStatus;

public interface PublicKeyRepositoryPort {
    public AuthorizationPublicKeyModel save(AuthorizationPublicKeyModel keyPair);
    public Optional<AuthorizationPublicKeyModel> findByElectionId(UUID electionId);
    public boolean existsKeyByElectionId(UUID electionId, KeyStatus status);
}
